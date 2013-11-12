package ru.fizteh.fivt.students.eltyshev.filemap.base;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractStorage<Key, Value> {
    class TransactionChanges {
        HashMap<Key, ValueDifference<Value>> modifiedData;
        int size;
        int uncommittedChanges;

        TransactionChanges() {
            this.modifiedData = new HashMap<Key, ValueDifference<Value>>();
            this.size = 0;
            this.uncommittedChanges = 0;
        }

        public void addChange(Key key, Value value) {
            if (modifiedData.containsKey(key)) {
                modifiedData.get(key).newValue = value;
            } else {
                modifiedData.put(key, new ValueDifference(oldData.get(key), value));
            }
        }

        public int applyChanges() {
            int recordsChanged = 0;
            for (final Key key : modifiedData.keySet()) {
                ValueDifference diff = modifiedData.get(key);
                if (!FileMapUtils.compareKeys(diff.oldValue, diff.newValue)) {
                    if (diff.newValue == null) {
                        oldData.remove(key);
                    } else {
                        oldData.put(key, (Value) diff.newValue);
                    }
                    recordsChanged += 1;
                }
            }
            return recordsChanged;
        }

        public int countChanges() {
            int recordsChanged = 0;
            for (final Key key : modifiedData.keySet()) {
                ValueDifference diff = modifiedData.get(key);
                if (!FileMapUtils.compareKeys(diff.oldValue, diff.newValue)) {
                    recordsChanged += 1;
                }
            }
            return recordsChanged;
        }

        public Value getValue(Key key) {
            if (modifiedData.containsKey(key)) {
                return modifiedData.get(key).newValue;
            }
            return oldData.get(key);
        }

        public int getSize() {
            return size;
        }

        public void decreaseSize() {
            size -= 1;
        }

        public void increaseSize() {
            size += 1;
        }

        public void increaseUncommittedChanges() {
            uncommittedChanges += 1;
        }

        public int getUncommittedChanges() {
            return uncommittedChanges;
        }

        public void clear() {
            modifiedData.clear();
            size = 0;
            uncommittedChanges = 0;
        }
    }

    private final Lock transactionLock = new ReentrantLock(true);

    public static final Charset CHARSET = StandardCharsets.UTF_8;
    // Data
    protected final HashMap<Key, Value> oldData;
    protected final ThreadLocal<TransactionChanges> transactionChanges = new ThreadLocal<TransactionChanges>() {
        @Override
        public TransactionChanges initialValue() {
            return new TransactionChanges();
        }
    };

    final private String tableName;
    private String directory;

    // Strategy
    protected abstract void load() throws IOException;

    protected abstract void save() throws IOException;

    // Constructor
    public AbstractStorage(String directory, String tableName) {
        this.directory = directory;
        this.tableName = tableName;
        oldData = new HashMap<Key, Value>();
        try {
            load();
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid file format");
        }
    }

    public int getUncommittedChangesCount() {
        return transactionChanges.get().getUncommittedChanges();
    }

    // Table implementation
    public String getName() {
        return tableName;
    }

    public Value storageGet(Key key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        return transactionChanges.get().getValue(key);
    }

    public Value storagePut(Key key, Value value) throws IllegalArgumentException {
        if (key == null || value == null) {
            String message = key == null ? "key " : "value ";
            throw new IllegalArgumentException(message + "cannot be null");
        }

        Value oldValue = transactionChanges.get().getValue(key);
        if (oldValue == null) {
            transactionChanges.get().increaseSize();
        }

        transactionChanges.get().addChange(key, value);
        return oldValue;
    }

    public Value storageRemove(Key key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (storageGet(key) == null) {
            return null;
        }

        Value oldValue = transactionChanges.get().getValue(key);
        transactionChanges.get().addChange(key, null);
        if (oldValue != null) {
            transactionChanges.get().decreaseSize();
        }
        transactionChanges.get().increaseUncommittedChanges();
        return oldValue;
    }

    public int storageSize() {
        return transactionChanges.get().getSize();
    }

    public int storageCommit() {
        try {
            transactionLock.lock();
            int recordsCommitted = transactionChanges.get().applyChanges();
            transactionChanges.get().clear();

            try {
                save();
            } catch (IOException e) {
                System.err.println("storageCommit: " + e.getMessage());
                return 0;
            }

            return recordsCommitted;
        } finally {
            transactionLock.unlock();
        }
    }

    public int storageRollback() {
        int recordsDeleted = transactionChanges.get().countChanges();
        transactionChanges.get().clear();
        return recordsDeleted;
    }

    public String getDirectory() {
        return directory;
    }

    void rawPut(Key key, Value value) {
        oldData.put(key, value);
    }

    Value rawGet(Key key) {
        return oldData.get(key);
    }
}

class ValueDifference<Value> {
    public Value oldValue;
    public Value newValue;

    ValueDifference(Value oldValue, Value newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}


