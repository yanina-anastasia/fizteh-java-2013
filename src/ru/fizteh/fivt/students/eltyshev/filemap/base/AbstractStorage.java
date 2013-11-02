package ru.fizteh.fivt.students.eltyshev.filemap.base;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractStorage<Key, Value> {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    // Data
    protected final HashMap<Key, Value> oldData;
    protected final HashMap<Key, ValueDifference<Value>> modifiedData;

    final private String tableName;
    private int size;
    private String directory;
    private int uncommittedChangesCount;

    // Strategy
    protected abstract void load() throws IOException;

    protected abstract void save() throws IOException;

    // Constructor
    public AbstractStorage(String directory, String tableName) {
        this.directory = directory;
        this.tableName = tableName;
        oldData = new HashMap<Key, Value>();
        modifiedData = new HashMap<Key, ValueDifference<Value>>();
        uncommittedChangesCount = 0;
        try {
            load();
        } catch (IOException e) {
            System.err.println("error loading table: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("error loading table: " + e.getMessage());
        }
    }

    public int getUncommittedChangesCount() {
        return uncommittedChangesCount;
    }

    // Table implementation
    public String getName() {
        return tableName;
    }

    public Value storageGet(Key key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        if (modifiedData.containsKey(key)) {
            return modifiedData.get(key).newValue;
        }

        return oldData.get(key);
    }

    public Value storagePut(Key key, Value value) throws IllegalArgumentException {
        if (key == null || value == null) {
            String message = key == null ? "key " : "value ";
            throw new IllegalArgumentException(message + "cannot be null");
        }
        Value oldValue = getOldValueFor(key);
        if (oldValue == null) {
            size += 1;
        }

        addChange(key, value);
        uncommittedChangesCount += 1;
        return oldValue;
    }

    public Value storageRemove(Key key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (storageGet(key) == null) {
            return null;
        }

        Value oldValue = getOldValueFor(key);
        addChange(key, null);
        if (oldValue != null) {
            size -= 1;
        }
        uncommittedChangesCount += 1;
        return oldValue;
    }

    public int storageSize() {
        return size;
    }

    public int storageCommit() {
        int recordsCommitted = 0;
        for (final Key key : modifiedData.keySet()) {
            ValueDifference diff = modifiedData.get(key);
            if (diff.oldValue != diff.newValue) {
                if (diff.newValue == null) {
                    oldData.remove(key);
                } else {
                    oldData.put(key, (Value) diff.newValue);
                }
                recordsCommitted += 1;
            }
        }
        modifiedData.clear();
        size = oldData.size();
        try {
            save();
        } catch (IOException e) {
            System.err.println("storageCommit: " + e.getMessage());
            return 0;
        }
        uncommittedChangesCount = 0;

        return recordsCommitted;
    }

    public int storageRollback() {
        int recordsDeleted = 0;
        for (final Key key : modifiedData.keySet()) {
            ValueDifference diff = modifiedData.get(key);
            if (diff.oldValue != diff.newValue) {
                recordsDeleted += 1;
            }
        }
        modifiedData.clear();
        size = oldData.size();

        uncommittedChangesCount = 0;

        return recordsDeleted;
    }

    public String getDirectory() {
        return directory;
    }

    // internal methods
    private Value getOldValueFor(Key key) {
        if (modifiedData.containsKey(key)) {
            return modifiedData.get(key).newValue;
        }
        return oldData.get(key);
    }

    private void addChange(Key key, Value value) {
        if (modifiedData.containsKey(key)) {
            modifiedData.get(key).newValue = value;
        } else {
            modifiedData.put(key, new ValueDifference(oldData.get(key), value));
        }
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
