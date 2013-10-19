package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class Table {
    private HashMap<String,String> hMap;

    public Table() {
        hMap = new HashMap<String,String>();
    }

    public boolean exists(String k) {
        return hMap.containsKey(k);
    }

    public void put(String k, String v) {
        hMap.put(k, v);
    }

    public String get(String k) {
        return hMap.get(k);
    }

    public void remove(String k) {
        hMap.remove(k);
    }

    public Set<Map.Entry<String,String>> listRows() {
        return hMap.entrySet();
    }
};
