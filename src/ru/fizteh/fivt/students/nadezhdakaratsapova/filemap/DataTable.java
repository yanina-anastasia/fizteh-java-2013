package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataTable implements Table {
    private String tableName;
    private Map<String, String> dataStorage = new HashMap<String, String>();

    public DataTable() {
    }

    public DataTable(String name) {
        tableName = name;
    }

    public String getName() {
        return tableName;
    }

    public String put(String key, String value) {
        String oldValue = dataStorage.get(key);
        dataStorage.put(key, value);
        return oldValue;
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

    public int size() {
        return dataStorage.size();
    }

    public int commit() {
        throw new UnsupportedOperationException("commit operation is not supported");
    }

    public int rollback() {
        throw new UnsupportedOperationException("rollback operation is not supported");
    }
}

