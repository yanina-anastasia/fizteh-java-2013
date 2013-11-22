package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.storage.structured.Storeable;

public class MultiHashMap {
    @SuppressWarnings("unchecked")
    HashMap<String, Storeable>[][] dbArray = (HashMap<String, Storeable>[][]) new HashMap[16][16];
    private int size = 0;
    
    public Storeable put(String key, Storeable value) {
        Storeable result = getMapForKey(key).put(key, value);
        if (result == null) {
            ++size;
        }
        return result;
    }
    
    public Storeable get(String key) {
        return getMapForKey(key).get(key);
    }

    public Storeable remove(String key) {
        Storeable result = getMapForKey(key).remove(key);
        if (result != null) {
            --size;
        }
        return result;
    }
    
    public boolean containsKey(String key) {
        return getMapForKey(key).containsKey(key);
    }
    
    public int size() {
        return size;
    }

    private HashMap<String, Storeable> getMapForKey(String key) {
        int b = Math.abs(key.getBytes()[0]);
        return dbArray[b % 16][b / 16 % 16];
    }
    
    public MultiHashMap(HashMap<String, Storeable> db) {
        this();
        for (Map.Entry<String, Storeable> entry : db.entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
            getMapForKey(key).put(key, value);
        }
    }

    public MultiHashMap() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                dbArray[i][j] = new HashMap<String, Storeable>();
            }
        }
    }
}
