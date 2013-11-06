package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.IOException;
import java.util.HashMap;

import ru.fizteh.fivt.storage.strings.Table;

public class NewTable implements Table {
    private String name;
    private HashMap<String, ValueState<String>> dataBaseMap = new HashMap<>();
    private NewTableProvider provider;
    
    public NewTable(String newName, NewTableProvider newProvider) {
        name = newName;
        provider = newProvider;
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
        if (!dataBaseMap.containsKey(key)) {
            return null;
        }
        return dataBaseMap.get(key).getValue();
    }
    public void loadCommittedValues(HashMap<String, String> load) {
        for (String key : load.keySet()) {
            ValueState<String> value = new ValueState<String>(load.get(key), load.get(key));
            dataBaseMap.put(key, value); 
        }
    }
    public HashMap<String, String> returnMap() {
        HashMap<String, String> map = new HashMap<>();
        for (String key: dataBaseMap.keySet()) {
            map.put(key, dataBaseMap.get(key).getCommitedValue());
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
            dataBaseMap.put(key, new ValueState<String>(null, value));
            result = null;
        }
        return result;
    }
    @Override
    public String remove(String key) {
        checkName(key);
        if (dataBaseMap.containsKey(key)) {
            String oldVal = dataBaseMap.get(key).getValue();
            dataBaseMap.get(key).setValue(null);
            return oldVal;
        } else {
            return null;
        }
    }
    @Override
    public int size() {
        int count = 0;
        for (ValueState<String> value : dataBaseMap.values()) {
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
    public int commit() throws RuntimeException {
        int count = 0;
        for (ValueState<String> value : dataBaseMap.values()) {
            if (value.commitValue()) {
                ++count;
            }
        }
        if (count != 0) {
            try {
                if (provider.getCurrentTableFile() != null) {
                    provider.saveChanges(provider.getCurrentTableFile());
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
           return 0;
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
