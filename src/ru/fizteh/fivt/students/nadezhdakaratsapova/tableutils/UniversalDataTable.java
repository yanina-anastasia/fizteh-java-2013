package ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils;


import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UniversalDataTable<ValueType> {
    public static final int DIR_COUNT = 16;
    public static final int FILE_COUNT = 16;

    private File dataBaseDirectory;
    private String tableName;
    private Map<String, ValueType> dataStorage = new HashMap<String, ValueType>();

    private Map<String, ValueType> putKeys = new HashMap<String, ValueType>();
    private Set<String> removeKeys = new HashSet<String>();

    public UniversalDataTable() {
    }

    public UniversalDataTable(String name) {
        tableName = name;
    }

    public UniversalDataTable(String name, File dir) {
        tableName = name;
        dataBaseDirectory = dir;
    }

    public String getName() {
        return tableName;
    }

    public ValueType put(String key, ValueType value) {
        ValueType oldValue = null;
        if (!removeKeys.contains(key)) {
            if ((oldValue = putKeys.get(key)) == null) {
                oldValue = dataStorage.get(key);
                if (oldValue == null) {
                    putKeys.put(key, value);
                } else {
                    if (!oldValue.equals(value)) {
                        putKeys.put(key, value);
                    }
                }
            } else {
                ValueType dataValue = dataStorage.get(key);
                if (dataValue == null) {
                    putKeys.put(key, value);
                } else {
                    if (!dataStorage.get(key).equals(value)) {
                        putKeys.put(key, value);
                    } else {
                        putKeys.remove(key);
                    }
                }

            }
        } else {
            ValueType dataValue = dataStorage.get(key);
            if (!dataValue.equals(value)) {
                putKeys.put(key, value);
            }
            removeKeys.remove(key);
        }
        return oldValue;
    }

    public Set<String> getKeys() {
        return dataStorage.keySet();
    }

    public ValueType get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Not correct key");
        }
        ValueType value = null;
        if (!putKeys.isEmpty()) {
            if (putKeys.containsKey(key)) {
                return putKeys.get(key);
            }
        }
        if (!removeKeys.contains(key)) {
            value = dataStorage.get(key);
            if (value == null) {
                value = putKeys.get(key);
            }
        }
        return value;
    }

    public ValueType remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Not correct key");
        }
        if (!putKeys.isEmpty()) {
            if (putKeys.get(key) != null) {
                if (dataStorage.get(key) != null) {
                    removeKeys.add(key);
                }
                return putKeys.remove(key);
            }
        }
        if (!removeKeys.isEmpty()) {
            if (removeKeys.contains(key)) {
                return null;
            }
        }
        ValueType value;
        if ((value = dataStorage.get(key)) != null) {
            removeKeys.add(key);
        }
        return value;
    }

    public boolean isEmpty() {
        return dataStorage.isEmpty();
    }

    public int size() {
        int size = dataStorage.size();
        Set<String> keysToCommit = putKeys.keySet();
        for (String key : keysToCommit) {
            if (!dataStorage.containsKey(key)) {
                ++size;
            }
        }
        size -= removeKeys.size();
        return size;
    }

    public int commit() {
        int commitSize = 0;
        if (!putKeys.isEmpty()) {
            Set<String> putKeysToCommit = putKeys.keySet();
            for (String key : putKeysToCommit) {
                dataStorage.put(key, putKeys.get(key));
                ++commitSize;
            }
            putKeys.clear();
        }
        if (!removeKeys.isEmpty()) {
            for (String key : removeKeys) {
                dataStorage.remove(key);
                ++commitSize;
            }
            removeKeys.clear();
        }
        return commitSize;
    }

    public int rollback() {
        int rollbackSize = 0;
        if (!putKeys.isEmpty()) {
            rollbackSize += putKeys.size();
            Set<String> putKeysToRollback = putKeys.keySet();
            for (String key : putKeysToRollback) {
                dataStorage.containsKey(key);
            }
            putKeys.clear();
        }
        if (!removeKeys.isEmpty()) {
            rollbackSize += removeKeys.size();
            removeKeys.clear();
        }
        return rollbackSize;
    }

    public int commitSize() {
        return putKeys.size() + removeKeys.size();
    }

    public File getWorkingDirectory() {
        return dataBaseDirectory;
    }

}
