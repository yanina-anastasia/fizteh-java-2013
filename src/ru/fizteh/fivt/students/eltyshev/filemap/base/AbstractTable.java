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
    protected final HashMap<String, String> modifiedData;
    protected final HashSet<String> deletedKeys;

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
        modifiedData = new HashMap<String, String>();
        deletedKeys = new HashSet<String>();
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
            return modifiedData.get(key);
        }
        if (deletedKeys.contains(key)) {
            return null;
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
        modifiedData.put(key, value);
        if (oldValue == null) {
            size += 1;
        }
        uncommittedChangesCount += 1;
        return oldValue;
    }

    public String remove(String key) throws IllegalArgumentException {
        if (key == null || key.equals("")) {
            throw new IllegalArgumentException("key cannot be null");
        }
        String oldValue = getOldValueFor(key);
        if (modifiedData.containsKey(key)) {
            modifiedData.remove(key);
            if (oldData.containsKey(key)) {
                deletedKeys.add(key);
            }
        } else {
            deletedKeys.add(key);
        }
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
        int recordsCommited = modifiedData.size() + deletedKeys.size();
        for (final String keyToDelete : deletedKeys) {
            oldData.remove(keyToDelete);
        }
        for (final String keyToAdd : modifiedData.keySet()) {
            oldData.put(keyToAdd, modifiedData.get(keyToAdd));
        }
        deletedKeys.clear();
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
        int recordsDeleted = modifiedData.size() + deletedKeys.size();
        deletedKeys.clear();
        modifiedData.clear();
        size = oldData.size();

        uncommittedChangesCount = 0;

        return recordsDeleted;
    }

    // internal methods
    protected String getDirectory() {
        return directory;
    }

    private String getOldValueFor(String key) {
        String oldValue = null;
        oldValue = modifiedData.get(key);
        // Если новое значение не было изменено\добавлено и не было удалено
        if (oldValue == null && !deletedKeys.contains(key)) {
            oldValue = oldData.get(key);
        }
        return oldValue;
    }
}
