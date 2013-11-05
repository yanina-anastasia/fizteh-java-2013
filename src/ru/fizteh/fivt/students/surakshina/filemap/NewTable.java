package ru.fizteh.fivt.students.surakshina.filemap;

import java.util.HashMap;

import ru.fizteh.fivt.storage.strings.Table;

public class NewTable implements Table {
    private String name;
    private HashMap<String, ValueState<String>> dataBaseMap = new HashMap<>();
    public NewTable(String name) {
        this.name = name;
    }
    @Override
    public String getName() {
        return name;
    }
    
    private void checkName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("key or value is null");
        }
        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Key or value is null");
        }
    }
    @Override
    public String get(String key) {
        checkName(key);
        if (dataBaseMap.containsKey(key)) {
            return dataBaseMap.get(key).getValue();
        } else {
            return null;
        }
    }
    public void loadCommittedValues(HashMap<String, String> load) {
        for (String key:load.keySet()) {
            ValueState<String> value = new ValueState<String>(load.get(key), load.get(key));
            dataBaseMap.put(key, value); 
        }
    }
    public HashMap<String, String> returnMap() {
        HashMap<String, String> map = new HashMap<>();
        for (String key: dataBaseMap.keySet()) {
            map.put(key, dataBaseMap.get(key).getValue());
        }
        return map;
    }
    @Override
    public String put(String key, String value) {
        checkName(key);
        checkName(value);
        String result;
        if (dataBaseMap.containsKey(key)) {
            result = dataBaseMap.get(key).getValue();
            dataBaseMap.get(key).setValue(value);
        } else {
            dataBaseMap.put(key, new ValueState<String>(value, value));
            result = null;
        }
        return result;
    }
    @Override
    public String remove(String key) {
        checkName(key);
        ValueState<String> value = null;
        if (dataBaseMap.containsKey(key)) {
        ValueState<String> newValue = new ValueState<String>(null, dataBaseMap.get(key).getValue());
        value = dataBaseMap.get(key);
        dataBaseMap.remove(key);
        dataBaseMap.put(key, newValue);
        }
        return value.getValue();
    }
    @Override
    public int size() {
        int count = 0;
        for (ValueState<String> value:dataBaseMap.values()) {
            if (value.getValue() != null) {
                ++count;
            }
        }
        return count;
    }
    public int unsavedChanges() {
        int count = 0;
        for (ValueState<String> value : dataBaseMap.values()) {
            if (value.needToCommit()) {
                ++count;
            }
        }
        return count;
    }
    @Override
    public int commit() {
        int count = 0;
        for (ValueState<String> value : dataBaseMap.values()) {
            if (value.commitValue()) {
                ++count;
            }
        }
        return count;
    }
    @Override
    public int rollback() {
        int count = 0;
        for (ValueState<String> value : dataBaseMap.values()) {
            if (value.rollbackValue()) {
                ++count;
            }
        }
        return count;
    }

}
