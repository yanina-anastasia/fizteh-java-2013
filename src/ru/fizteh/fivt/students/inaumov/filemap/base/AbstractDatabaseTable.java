package ru.fizteh.fivt.students.inaumov.filemap.base;

import ru.fizteh.fivt.students.inaumov.filemap.FileMapUtils;
import java.io.IOException;
import java.util.HashMap;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractDatabaseTable<Key, Value> {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public final Lock transactionLock = new ReentrantLock(true);
    public HashMap<Key, Value> keyValueHashMap = new HashMap<Key, Value>();
    private class Diff {
        private HashMap<Key, Value> modifiedKeyValueHashMap = new HashMap<Key, Value>();
        private int size = 0;
        private int unsavedChangesNumber = 0;

        public void change(Key key, Value value) {
            modifiedKeyValueHashMap.put(key, value);
        }

        public int commitChanges() {
            int recordsChangedCount = 0;
            for (final Key key: modifiedKeyValueHashMap.keySet()) {
                Value newValue = modifiedKeyValueHashMap.get(key);
                if (!FileMapUtils.isEqual(keyValueHashMap.get(key), newValue)) {
                    if (newValue == null) {
                        keyValueHashMap.remove(key);
                    } else {
                        keyValueHashMap.put(key, (Value) newValue);
                    }
                    recordsChangedCount += 1;
                }
            }

            return recordsChangedCount;
        }

        public int getChangesCount() {
            int recordsChangedCount = 0;
            for (final Key key: modifiedKeyValueHashMap.keySet()) {
                Value newValue = modifiedKeyValueHashMap.get(key);
                if (!FileMapUtils.isEqual(keyValueHashMap.get(key), newValue)) {
                    recordsChangedCount += 1;
                }
            }

            return recordsChangedCount;
        }

        public int calcSize() {
            int tableSize = 0;
            for (final Key key: modifiedKeyValueHashMap.keySet()) {
                Value newValue = modifiedKeyValueHashMap.get(key);
                Value oldValue = keyValueHashMap.get(key);
                if (newValue == null && oldValue != null) {
                    tableSize -= 1;
                }
                if (newValue != null && oldValue == null) {
                    tableSize += 1;
                }
            }

            return tableSize;
        }

        public Value getValue(Key key) {
            if (modifiedKeyValueHashMap.containsKey(key)) {
                return modifiedKeyValueHashMap.get(key);
            }

            return keyValueHashMap.get(key);
        }

        public int getSize() {
            return calcSize() + keyValueHashMap.size();
        }

        public void incUnsavedChangesNumber() {
            unsavedChangesNumber += 1;
        }

        public int getUnsavedChangesNumber() {
            return unsavedChangesNumber;
        }

        public void clear() {
            modifiedKeyValueHashMap.clear();
            size = 0;
            unsavedChangesNumber = 0;
        }
    }
    public final ThreadLocal<Diff> diff = new ThreadLocal<Diff>() {
        @Override
        public Diff initialValue() {
            return new Diff();
        }
    };

    private final String tableName;
    private final String tableDir;

    protected abstract void loadTable() throws IOException;

    protected abstract void saveTable() throws IOException;

    public AbstractDatabaseTable(String tableDir, String tableName) {
        if (FileMapUtils.isStringNullOrEmpty(tableDir)) {
            throw new IllegalArgumentException("error: selected directory is null (or empty)");
        }
        if (FileMapUtils.isStringNullOrEmpty(tableName)) {
            throw new IllegalArgumentException("error: selected database name is null (or empty)");
        }

        this.tableName = tableName;
        this.tableDir = tableDir;

        try {
            loadTable();
        } catch (IOException e) {
            throw new IllegalArgumentException("error: can't load table, incorrect file format");
        }
    }

    public String getName() {
        return tableName;
    }

    public String getDir() {
        return tableDir;
    }

    public Value tableGet(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("error: selected key is null");
        }

        return diff.get().getValue(key);
    }

    public Value tablePut(Key key, Value value) {
        if (key == null) {
            throw new IllegalArgumentException("error: selected key is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("error: selected value is null");
        }

        Value oldValue = diff.get().getValue(key);
        diff.get().change(key, value);

        return oldValue;
    }

    public Value tableRemove(Key key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("error: selected key is null");
        }
        if (tableGet(key) == null) {
            return null;
        }

        Value oldValue = diff.get().getValue(key);
        diff.get().change(key, null);
        diff.get().incUnsavedChangesNumber();

        return oldValue;
    }

    public int tableCommit() {
        try {
            transactionLock.lock();
            int commitedChangesNumber = diff.get().commitChanges();
            diff.get().clear();

            try {
                saveTable();
            } catch (IOException e) {
                System.err.println("error: can't save table: " + e.getMessage());
            }

            return commitedChangesNumber;
        } finally {
            transactionLock.unlock();
        }
    }

    public int tableRollback() {
        int rollbackedChangesCount = diff.get().getChangesCount();
        diff.get().clear();

        return rollbackedChangesCount;
    }

    public int tableSize() {
        return diff.get().getSize();
    }

    public int getUnsavedChangesNumber() {
        return diff.get().getUnsavedChangesNumber();
    }

    public void rawPut(Key key, Value value) {
        keyValueHashMap.put(key, value);
    }

    public Value rawGet(Key key) {
        return keyValueHashMap.get(key);
    }
}
