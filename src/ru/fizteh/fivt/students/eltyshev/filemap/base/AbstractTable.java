package ru.fizteh.fivt.students.eltyshev.filemap.base;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

public abstract class AbstractTable implements Table {
    protected static final Charset CHARSET = StandardCharsets.UTF_8;
    // Data
    protected final HashMap<String, String> oldData;
    protected final HashMap<String, ValueDifference> modifiedData;

    final private String tableName;
    private int size;
    private String directory;
    private int uncommittedChangesCount;

    // Strategy
    protected abstract void load() throws IOException;

    protected abstract void save() throws IOException;

    // Constructor
    public AbstractTable(String directory, String tableName) {
        this.directory = directory;
        this.tableName = tableName;
        oldData = new HashMap<String, String>();
        modifiedData = new HashMap<String, ValueDifference>();
        uncommittedChangesCount = 0;
        try {
            load();
        } catch (IOException e) {
            System.err.println("error loading table: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("error loading table: " + e.getMessage());
        }
    }

    public int getUncommitedChangesCount() {
        return uncommittedChangesCount;
    }

    // Table implementation
    public String getName() {
        return tableName;
    }

    public String get(String key) throws IllegalArgumentException {
        if (key == null || key.equals("")) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        if (modifiedData.containsKey(key)) {
            return modifiedData.get(key).newValue;
        }

        return oldData.get(key);
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            String message = key == null ? "key " : "value ";
            throw new IllegalArgumentException(message + "cannot be null");
        }
        if (key.equals("") || value.equals("") || key.trim().isEmpty() || value.trim().isEmpty()) {
            String message = key.equals("") ? "key " : "value ";
            throw new IllegalArgumentException(message + "cannot be empty");
        }

        String oldValue = getOldValueFor(key);
        if (oldValue == null) {
            size += 1;
        }

        addChange(key, value);
        uncommittedChangesCount += 1;
        return oldValue;
    }

    public String remove(String key) throws IllegalArgumentException {
        if (key == null || key.equals("")) {
            throw new IllegalArgumentException("key cannot be null");
        }

        if (get(key) == null) {
            return null;
        }

        String oldValue = getOldValueFor(key);
        addChange(key, null);
        if (oldValue != null) {
            size -= 1;
        }
        uncommittedChangesCount += 1;
        return oldValue;
    }

    public int size() {
        return size;
    }

    public int commit() {
        int recordsCommited = 0;
        for (final String key : modifiedData.keySet()) {
            ValueDifference diff = modifiedData.get(key);
            if (diff.oldValue != diff.newValue) {
                if (diff.newValue == null) {
                    oldData.remove(key);
                } else {
                    oldData.put(key, diff.newValue);
                }
                recordsCommited += 1;
            }
        }
        modifiedData.clear();
        size = oldData.size();
        try {
            save();
        } catch (IOException e) {
            System.err.println("commit: " + e.getMessage());
            return 0;
        }
        uncommittedChangesCount = 0;

        return recordsCommited;
    }

    public int rollback() {
        int recordsDeleted = 0;
        for (final String key : modifiedData.keySet()) {
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

    // internal methods
    protected String getDirectory() {
        return directory;
    }

    private void addChange(String key, String value) {
        if (modifiedData.containsKey(key)) {
            modifiedData.get(key).newValue = value;
        } else {
            modifiedData.put(key, new ValueDifference(oldData.get(key), value));
        }
    }

    private String getOldValueFor(String key) {
        if (modifiedData.containsKey(key)) {
            return modifiedData.get(key).newValue;
        }
        return oldData.get(key);
    }
}

class ValueDifference {
    public String oldValue;
    public String newValue;

    ValueDifference(String oldValue, String newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
