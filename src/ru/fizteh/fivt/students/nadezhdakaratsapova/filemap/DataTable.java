package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataTable {
    private Map<String, String> dataStorage = new HashMap<String, String>();

    public void add(String key, String value) {
        dataStorage.put(key, value);
    }

    public Set<String> getKeys() {
        return dataStorage.keySet();
    }

    public String get(String key) {
        return dataStorage.get(key);
    }

    public String remove(String key) {
        return dataStorage.remove(key);
    }

    public boolean isEmpty() {
        return dataStorage.isEmpty();
    }
}
