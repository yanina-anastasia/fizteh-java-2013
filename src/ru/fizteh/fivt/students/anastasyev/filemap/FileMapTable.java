package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.*;

public class FileMapTable implements Table {
    private File currentFileMapTable;
    private FileMap[][] mapsTable;
    private HashMap<String, Value> changedKeys = new HashMap<String, Value>();
    private ArrayList<Class<?>> columnTypes;
    private FileMapTableProvider provider;
    private int size = 0;

    public int indexOfColumn(Class<?> column) {
        return columnTypes.indexOf(column);
    }

    private class Value {
        private Storeable value;
        private Storeable onDiskValue;

        public Value(Storeable newValue, Storeable newOnDisk) {
            value = newValue;
            onDiskValue = newOnDisk;
        }

        public Storeable getValue() {
            return value;
        }

        public Storeable getOnDisk() {
            return onDiskValue;
        }
    }

    private boolean isEmptyString(String val) {
        return (val == null || (val.isEmpty() || val.trim().isEmpty()));
    }

    private void checkValueCorrectness(Storeable value) throws ColumnFormatException {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        int i = 0;
        for (; i < columnTypes.size(); ++i) {
            try {
                if (value.getColumnAt(i) != null) {
                    if (!columnTypes.get(i).equals(value.getColumnAt(i).getClass())) {
                        throw new ColumnFormatException("Wrong column format");
                    }
                }
                if (columnTypes.get(i).equals(String.class) && value.getColumnAt(i) != null
                        && value.getStringAt(i) != null && value.getStringAt(i).trim().isEmpty()) {
                    throw new IllegalArgumentException("empty string in value");
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("Wrong column count");
            }
        }
        try {
            if (value.getColumnAt(i) != null) {
                throw new ColumnFormatException("Wrong column count");
            }
        } catch (IndexOutOfBoundsException e) {
            //It's OK   Некрасивая конструкция, and I know it
        }
    }

    public FileMap getMyState(int hashCode) {
        int absHash = Math.abs(hashCode);
        return mapsTable[absHash % 16][absHash / 16 % 16];
    }

    public FileMap openFileMap(int hashCode) throws IOException {
        int absHash = Math.abs(hashCode);
        int dirHash = absHash % 16;
        int datHash = absHash / 16 % 16;
        if (mapsTable[dirHash][datHash] == null) {
            File dir = new File(currentFileMapTable.toString() + File.separator + dirHash + ".dir");
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new IOException("Can't create " + dirHash + ".dir");
                }
            }
            String datName = dir.toString() + File.separator + datHash + ".dat";
            try {
                mapsTable[dirHash][datHash] = new FileMap(datName, dirHash, datHash, this, provider);
            } catch (ParseException e) {
                throw new IOException("incorrect value format", e);
            }
        }
        return mapsTable[dirHash][datHash];
    }

    public void deleteFileMap(int hashCode) throws IOException {
        int absHash = Math.abs(hashCode);
        mapsTable[absHash % 16][absHash / 16 % 16].delete();
        mapsTable[absHash % 16][absHash / 16 % 16] = null;
    }

    public void dropTable() throws IOException {
        currentFileMapTable = null;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (mapsTable[i][j] != null) {
                    mapsTable[i][j].delete();
                    mapsTable[i][j] = null;
                }
            }
        }
    }

    private void readTable() throws IOException, ParseException {
        mapsTable = new FileMap[16][16];
        size = 0;
        for (int i = 0; i < 16; ++i) {
            File dbDir = new File(currentFileMapTable.toString() + File.separator + i + ".dir");
            if (dbDir.exists()) {
                if (!dbDir.isDirectory()) {
                    throw new IOException(i + ".dir is not table subdirectory");
                }
                for (int j = 0; j < 16; ++j) {
                    File dbDat = new File(dbDir.toString() + File.separator + j + ".dat");
                    if (dbDat.exists()) {
                        if (!dbDat.isFile()) {
                            throw new IOException(i + ".dat is not a FileMap file");
                        }
                        mapsTable[i][j] = new FileMap(dbDat.toString(), i, j, this, provider);
                        if (mapsTable[i][j].isEmpty()) {
                            mapsTable[i][j].delete();
                            mapsTable[i][j] = null;
                        } else {
                            size += mapsTable[i][j].size();
                        }
                    }
                }
                if (dbDir.exists()) {
                    if (dbDir.listFiles().length == 0) {
                        if (!dbDir.delete()) {
                            throw new IOException("Can't remove empty fileMaps directory");
                        }
                    }
                }
            }
        }
    }

    private boolean storeableEquals(Storeable first, Storeable second) {
        if (first == null && second == null) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        for (int i = 0; i < columnTypes.size(); ++i) {
            Object val1 = first.getColumnAt(i);
            Object val2 = second.getColumnAt(i);
            if (val1 == null) {
                if (val2 != null) {
                    return false;
                }
            } else if (!val1.equals(val2)) {
                return false;
            }
        }
        return true;
    }

    private int changesCount() {
        int changesCount = 0;
        for (Map.Entry<String, Value> entry : changedKeys.entrySet()) {
            if (entry.getValue().value != null && !storeableEquals(entry.getValue().value, entry.getValue().onDiskValue)
                    || (entry.getValue().value == null && entry.getValue().getOnDisk() != null)) {
                ++changesCount;
            }
        }
        return changesCount;
    }

    private void readSignature() throws IOException {
        File signature = new File(currentFileMapTable, "signature.tsv");
        if (!signature.exists()) {
            throw new IOException("signature.tsv doesn't exists");
        }
        if (signature.length() == 0) {
            throw new IOException("signature.tsv is empty");
        }
        columnTypes = new ArrayList<Class<?>>();
        try (RandomAccessFile input = new RandomAccessFile(signature.toString(), "r")) {
            int position = 0;
            while (input.getFilePointer() != input.length()) {
                byte ch = 0;
                Vector<Byte> v = new Vector<Byte>();
                ch = input.readByte();
                while (ch != ' ') {
                    v.add(ch);
                    ch = input.readByte();
                }
                byte[] res = new byte[v.size()];
                for (int i = 0; i < v.size(); i++) {
                    res[i] = v.elementAt(i).byteValue();
                }
                String type = new String(res, "UTF-8");
                Class<?> classType = provider.getClassName(type);
                columnTypes.add(classType);
            }
        }
    }

    public FileMapTable(String tableName, FileMapTableProvider newProvider) throws IOException, ParseException {
        currentFileMapTable = new File(tableName);
        provider = newProvider;
        if (!currentFileMapTable.exists()) {
            if (!currentFileMapTable.mkdir()) {
                throw new IOException("Can't create " + currentFileMapTable.getName());
            }
        }
        if (!currentFileMapTable.isDirectory()) {
            throw new IOException(currentFileMapTable.getName() + " is not a directory");
        }
        readSignature();
        readTable();
    }

    public FileMapTable(String tableName, List<Class<?>> newColumnTypes, FileMapTableProvider newProvider)
            throws IOException, ParseException {
        currentFileMapTable = new File(tableName);
        if (!currentFileMapTable.exists()) {
            if (!currentFileMapTable.mkdir()) {
                throw new IOException("Can't create " + currentFileMapTable.getName());
            }
        }
        if (!currentFileMapTable.isDirectory()) {
            throw new IOException(currentFileMapTable.getName() + " is not a directory");
        }
        columnTypes = new ArrayList<Class<?>>(newColumnTypes);
        provider = newProvider;
        readTable();
    }

    @Override
    public String getName() {
        return currentFileMapTable.getName();
    }

    @Override
    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        if (isEmptyString(key) || key.split("\\s").length > 1) {
            throw new IllegalArgumentException("Wrong key");
        }
        checkValueCorrectness(value);
        int absHash = Math.abs(key.hashCode());
        int dirHash = absHash % 16;
        int datHash = absHash / 16 % 16;
        try {
            mapsTable[dirHash][datHash] = openFileMap(absHash);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't open fileMap");
        }
        Storeable str = mapsTable[dirHash][datHash].put(key, value);
        Value element = changedKeys.get(key);
        if (str == null) {
            ++size;
            if (element == null) {
                changedKeys.put(key, new Value(value, null));
            } else {
                changedKeys.put(key, new Value(value, element.getOnDisk()));
            }
            return null;
        } else {
            if (element == null) {
                changedKeys.put(key, new Value(value, str));
            } else {
                changedKeys.put(key, new Value(value, element.getOnDisk()));
            }
            return str;
        }
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        if (isEmptyString(key) || key.contains(" ")) {
            throw new IllegalArgumentException("Wrong key");
        }
        int absHash = Math.abs(key.hashCode());
        int dirHash = absHash % 16;
        int datHash = absHash / 16 % 16;
        if (mapsTable[dirHash][datHash] == null) {
            return null;
        }
        Storeable str = mapsTable[dirHash][datHash].remove(key);
        if (str == null) {
            return null;
        } else {
            --size;
            Value element = changedKeys.get(key);
            if (element == null) {
                changedKeys.put(key, new Value(null, str));
            } else {
                changedKeys.put(key, new Value(null, element.getOnDisk()));
            }
            return str;
        }
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        if (isEmptyString(key) || key.contains(" ")) {
            throw new IllegalArgumentException("Wrong key");
        }
        int absHash = Math.abs(key.hashCode());
        int dirHash = absHash % 16;
        int datHash = absHash / 16 % 16;
        if (mapsTable[dirHash][datHash] == null) {
            return null;
        }
        return mapsTable[dirHash][datHash].get(key);
    }

    @Override
    public int commit() throws RuntimeException {
        int changesCount = changesCount();
        changedKeys.clear();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (mapsTable[i][j] != null) {
                    try {
                        mapsTable[i][j].save();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    if (mapsTable[i][j].isEmpty()) {
                        try {
                            mapsTable[i][j].delete();
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }
                }
            }
        }
        return changesCount;
    }

    @Override
    public int rollback() throws RuntimeException {
        int changesCount = changesCount();
        for (String key : changedKeys.keySet()) {
            if (changedKeys.get(key).getOnDisk() == null) {
                getMyState(key.hashCode()).remove(key);
            } else {
                getMyState(key.hashCode()).put(key, changedKeys.get(key).getOnDisk());
            }
        }
        changedKeys.clear();
        return changesCount;
    }

    @Override
    public int size() {
        return size;
    }

    public int uncommittedSize() {
        return changesCount();
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException(columnIndex + " outOfBounds");
        }
        return columnTypes.get(columnIndex);
    }
}
