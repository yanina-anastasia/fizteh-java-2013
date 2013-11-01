package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.*;

public class DataTable implements Table {
    private String tableName;
    private Map<String, String> dataStorage = new HashMap<String, String>();
    private Map<String, String> putKeys = new HashMap<String, String>();
    private Set<String> removeKeys = new HashSet<String>();


    public DataTable() {
    }

    public DataTable(String name) {
        tableName = name;
    }

    public String getName() {
        return tableName;
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if ((key == null) || (key.isEmpty()) || (value == null)) {
            throw new IllegalArgumentException("Not correct key or value");
        }
        String oldValue = null;
        if (!removeKeys.contains(key)) {
            if ((oldValue = putKeys.get(key)) == null) {
                oldValue = dataStorage.get(key);
                putKeys.put(key, value);
            } else {
                putKeys.put(key, value);
            }
        } else {
            putKeys.put(key, value);
            removeKeys.remove(key);
        }
        return oldValue;
    }

    public Set<String> getKeys() {
        return dataStorage.keySet();
    }

    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Not correct key");
        }
        String value = null;
        if (!removeKeys.contains(key)) {
            value = dataStorage.get(key);
            if (value == null) {
                value = putKeys.get(key);
            }
        }
        return value;
    }

    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Not correct key");
        }
        if (!putKeys.isEmpty()) {
            if (putKeys.get(key) != null) {
                removeKeys.add(key);
                return putKeys.remove(key);
            }
        }
        if (dataStorage.get(key) != null) {
            removeKeys.add(key);
        }
        return dataStorage.remove(key);
    }

    public boolean isEmpty() {
        return dataStorage.isEmpty();
    }

    public int size() {
        return dataStorage.size();
    }

    public int commit() {
        int commitSize = 0;
        if (!putKeys.isEmpty()) {
            Set<String> putKeysToCommit = putKeys.keySet();
            for (String key : putKeysToCommit) {
                if (dataStorage.put(key, putKeys.get(key)) == null) {
                    ++commitSize;
                }
            }
            putKeys.clear();
        }
        if (!removeKeys.isEmpty()) {
            for (String key : removeKeys) {
                dataStorage.remove(key);
            }
            removeKeys.clear();
        }
        return commitSize;
    }

    public int rollback() {
        int rollbackSize = 0;
        if (!putKeys.isEmpty()) {
            Set<String> putKeysToRollback = putKeys.keySet();
            for (String key : putKeysToRollback) {
                if (!dataStorage.containsKey(key)) {
                    ++rollbackSize;
                }
            }
            putKeys.clear();
        }
        if (!removeKeys.isEmpty()) {
            removeKeys.clear();
        }
        return rollbackSize;
    }

    public int commitSize() {
        int commitSize = 0;
        if (!putKeys.isEmpty()) {
            commitSize += putKeys.size();
        }
        if (!removeKeys.isEmpty()) {
            commitSize += removeKeys.size();
        }
        return commitSize;
    }
}

