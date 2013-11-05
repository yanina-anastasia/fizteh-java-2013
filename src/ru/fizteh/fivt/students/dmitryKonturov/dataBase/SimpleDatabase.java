package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  Может быть этот класс и ненужен.
 *
 */

class SimpleDatabase {
    private HashMap<String, Object> dbMap;

    public SimpleDatabase() {
        dbMap = new HashMap<>();
    }

    public Object get(String key) {
        return dbMap.get(key);
    }

    public Object put(String key, Object value) {
        return dbMap.put(key, value);
    }

    public Object remove(String key) {
        return dbMap.remove(key);
    }

    Set<Map.Entry<String, Object>> getEntries() {
        return dbMap.entrySet();
    }
}
