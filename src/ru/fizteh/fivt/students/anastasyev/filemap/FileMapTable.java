package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileMapTable implements Table {
    private File currentFileMapTable;
    private ArrayList<Class<?>> columnTypes;
    private FileMapTableProvider provider;
    private FileMap[][] mapsTable;

    private ThreadLocal<HashMap<String, Storeable>> changedKeys = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        public HashMap<String, Storeable> initialValue() {
            return new HashMap<String, Storeable>();
        }
    };
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock read = readWriteLock.readLock();
    private Lock write = readWriteLock.writeLock();

    private Storeable onDiskValue(String key) {
        int absHash = Math.abs(key.hashCode());
        int dirHash = absHash % 16;
        int datHash = absHash / 16 % 16;
        Storeable valueOnDisk = null;
        if (mapsTable[dirHash][datHash] != null) {
            valueOnDisk = mapsTable[dirHash][datHash].get(key);
        }
        return valueOnDisk;
    }

    private int changesCount() {
        int changesCount = 0;
        for (String entryKey : changedKeys.get().keySet()) {
            Storeable value = changedKeys.get().get(entryKey);
            if (value != null && !storeableEquals(value, onDiskValue(entryKey))
                    || value == null && onDiskValue(entryKey) != null) {
                ++changesCount;
            }
        }
        return changesCount;
    }

    private int changesSize() {
        int changesSize = 0;
        for (String entryKey : changedKeys.get().keySet()) {
            Storeable value = changedKeys.get().get(entryKey);
            if (value == null) {
                if (onDiskValue(entryKey) != null) {
                    --changesSize;
                }
            } else {
                if (onDiskValue(entryKey) == null) {
                    ++changesSize;
                }
            }
        }
        return changesSize;
    }

    private int oldSize() {
        int oldSize = 0;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (mapsTable[i][j] != null) {
                    oldSize += mapsTable[i][j].size();
                }
            }
        }
        return oldSize;
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
                    throw new IllegalArgumentException("empty string in newValue");
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

    public FileMap openFileMap(int dirHash, int datHash) throws IOException {
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
                throw new IOException("incorrect newValue format", e);
            }
        }
        return mapsTable[dirHash][datHash];
    }

    private void readTable() throws IOException, ParseException {
        mapsTable = new FileMap[16][16];
        for (int i = 0; i < 16; ++i) {
            File dbDir = new File(currentFileMapTable.toString(), i + ".dir");
            if (dbDir.exists()) {
                if (!dbDir.isDirectory()) {
                    throw new IOException(i + ".dir is not table subdirectory");
                }
                if (dbDir.listFiles().length == 0) {
                    throw new IOException(i + ".dir is empty dir");
                }
                for (int j = 0; j < 16; ++j) {
                    File dbDat = new File(dbDir.toString(), j + ".dat");
                    if (dbDat.exists()) {
                        if (!dbDat.isFile()) {
                            throw new IOException(i + ".dat is not a FileMap file");
                        }
                        if (dbDat.length() == 0) {
                            throw new IOException(i + ".dat is empty");
                        }
                        mapsTable[i][j] = new FileMap(dbDat.toString(), i, j, this, provider);
                        if (mapsTable[i][j].isEmpty()) {
                            mapsTable[i][j].delete();
                            mapsTable[i][j] = null;
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
            while (input.getFilePointer() != input.length()) {
                byte ch = 0;
                Vector<Byte> v = new Vector<Byte>();
                while (ch != ' ' && input.getFilePointer() != input.length()) {
                    ch = input.readByte();
                    v.add(ch);
                }
                byte[] res = new byte[v.size()];
                for (int i = 0; i < v.size(); i++) {
                    res[i] = v.elementAt(i).byteValue();
                }
                String type = new String(res, "UTF-8");
                type = type.trim();
                Class<?> classType = provider.getClassName(type);
                if (classType == null) {
                    throw new IOException("signature.tsv is broken");
                }
                columnTypes.add(classType);
            }
        }
    }

    public FileMapTable(String tableName, FileMapTableProvider newProvider) throws IOException, ParseException {
        try {
            read.lock();
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
        } finally {
            read.unlock();
        }
    }

    public FileMapTable(String tableName, List<Class<?>> newColumnTypes, FileMapTableProvider newProvider)
            throws IOException, ParseException {
        try {
            read.lock();
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
        } finally {
            read.unlock();
        }
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
        try {
            read.lock();
            Storeable valueOnDisk = onDiskValue(key);
            Storeable valueChanged = changedKeys.get().get(key);
            if (!changedKeys.get().containsKey(key)) {
                changedKeys.get().put(key, value);
                return valueOnDisk;
            }
            if (storeableEquals(value, valueOnDisk)) {
                changedKeys.get().remove(key);
            } else {
                changedKeys.get().put(key, value);
            }
            return valueChanged;
        } finally {
            read.unlock();
        }
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        if (isEmptyString(key) || key.split("\\s").length > 1) {
            throw new IllegalArgumentException("Wrong key");
        }
        try {
            read.lock();
            Storeable valueOnDisk = onDiskValue(key);
            Storeable valueChanged = changedKeys.get().get(key);
            if (valueOnDisk == null) {
                if (!changedKeys.get().containsKey(key)) {
                    return null;
                } else {
                    changedKeys.get().remove(key);
                    return valueChanged;
                }
            } else {
                if (!changedKeys.get().containsKey(key)) {
                    changedKeys.get().put(key, null);
                    return valueOnDisk;
                }
                if (valueChanged != null) {
                    changedKeys.get().put(key, null);
                    return valueChanged;
                }
                return null;
            }
        } finally {
            read.unlock();
        }
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        if (isEmptyString(key) || key.split("\\s").length > 1) {
            throw new IllegalArgumentException("Wrong key");
        }
        try {
            read.lock();
            if (changedKeys.get().containsKey(key)) {
                return changedKeys.get().get(key);
            } else {
                return onDiskValue(key);
            }
        } finally {
            read.unlock();
        }
    }

    @Override
    public int commit() throws RuntimeException {
        try {
            write.lock();
            int changesCount = 0;
            for (Map.Entry<String, Storeable> entry : changedKeys.get().entrySet()) {
                String key = entry.getKey();
                Storeable value = entry.getValue();
                if (value != null && !storeableEquals(value, onDiskValue(key))
                        || value == null && onDiskValue(key) != null) {
                    int absHash = Math.abs(key.hashCode());
                    int dirHash = absHash % 16;
                    int datHash = absHash / 16 % 16;
                    try {
                        mapsTable[dirHash][datHash] = openFileMap(dirHash, datHash);
                    } catch (IOException e) {
                        throw new IllegalArgumentException("Can't open fileMap");
                    }
                    if (value == null) {
                        mapsTable[dirHash][datHash].remove(key);
                    } else {
                        mapsTable[dirHash][datHash].put(key, value);
                    }
                    ++changesCount;
                }
            }
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
            changedKeys.get().clear();
            return changesCount;
        } finally {
            write.unlock();
        }
    }

    @Override
    public int rollback() throws RuntimeException {
        int changesCount = changesCount();
        changedKeys.get().clear();
        return changesCount;
    }

    @Override
    public int size() {
        return oldSize() + changesSize();
    }

    public int uncommittedChangesCount() {
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
