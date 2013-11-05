package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileMapTable implements Table {
    private File currentFileMapTable;
    private FileMap[][] mapsTable;
    private HashMap<String, Value> changedKeys = new HashMap<String, Value>();
    int size = 0;

    private class Value {
        private String value;
        private String onDiskValue;

        public Value(String newValue, String newOnDisk) {
            value = newValue;
            onDiskValue = newOnDisk;
        }

        public String getValue() {
            return value;
        }

        public String getOnDisk() {
            return onDiskValue;
        }
    }

    private boolean isEmptyString(String val) {
        return (val == null || (val.isEmpty() || val.trim().isEmpty()));
    }

    public FileMap getMyState(int hashCode) throws IOException {
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
            mapsTable[dirHash][datHash] = new FileMap(datName, dirHash, datHash);
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

    private void readTable() throws IOException {
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
                        mapsTable[i][j] = new FileMap(dbDat.toString(), i, j);
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

    private int changesCount() {
        int changesCount = 0;
        for (Map.Entry<String, Value> entry : changedKeys.entrySet()) {
            if (entry.getValue().value != null && !entry.getValue().value.equals(entry.getValue().onDiskValue)
                    || (entry.getValue().value == null && entry.getValue().getOnDisk() != null)) {
                ++changesCount;
            }
        }
        return changesCount;
    }

    public FileMapTable(String tableName) throws IOException {
        currentFileMapTable = new File(tableName);
        if (!currentFileMapTable.exists()) {
            if (!currentFileMapTable.mkdir()) {
                throw new IOException("Can't create " + currentFileMapTable.getName());
            }
        }
        if (!currentFileMapTable.isDirectory()) {
            throw new IOException(currentFileMapTable.getName() + " is not a directory");
        }
        readTable();
    }

    @Override
    public String getName() {
        return currentFileMapTable.getName();
    }

    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        if (isEmptyString(key) || isEmptyString(value)) {
            throw new IllegalArgumentException();
        }
        int absHash = Math.abs(key.hashCode());
        int dirHash = absHash % 16;
        int datHash = absHash / 16 % 16;
        try {
            mapsTable[dirHash][datHash] = openFileMap(absHash);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't open fileMap");
        }
        String str = mapsTable[dirHash][datHash].put(key, value);
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
    public String remove(String key) throws IllegalArgumentException {
        if (isEmptyString(key)) {
            throw new IllegalArgumentException();
        }
        int absHash = Math.abs(key.hashCode());
        int dirHash = absHash % 16;
        int datHash = absHash / 16 % 16;
        if (mapsTable[dirHash][datHash] == null) {
            return null;
        }
        String str = mapsTable[dirHash][datHash].remove(key);
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
    public String get(String key) throws IllegalArgumentException {
        if (isEmptyString(key)) {
            throw new IllegalArgumentException();
        }
        int absHash = Math.abs(key.hashCode());
        int dirHash = absHash % 16;
        int datHash = absHash / 16 % 16;
        if (mapsTable[dirHash][datHash] == null) {
            return null;
        }
        String str = mapsTable[dirHash][datHash].get(key);
        if (str.equals("not found")) {
            return null;
        } else {
            return str;
        }
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
        try {
            readTable();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        int changesCount = changesCount();
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
}
