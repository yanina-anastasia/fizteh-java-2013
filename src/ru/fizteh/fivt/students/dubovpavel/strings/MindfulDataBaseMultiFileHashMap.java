package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DataBaseMultiFileHashMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MindfulDataBaseMultiFileHashMap<V> extends DataBaseMultiFileHashMap<V> {
    private HashMap<String, V> oldDict;
    protected ObjectTransformer<V> transformer;
    MindfulDataBaseMultiFileHashMap(File path, ObjectTransformer<V> transformer) {
        super(path, transformer);
        oldDict = new HashMap<>();
        this.transformer = transformer;
    }

    private void copyHashMap(HashMap<String, V> from, HashMap<String, V> to) {
        to.clear();
        for(Map.Entry<String, V> entry: from.entrySet()) {
            to.put(entry.getKey(), transformer.copy(entry.getValue()));
        }
    }

    @Override
    public void open() throws DataBaseException {
        try {
            super.open();
        } finally {
            copyHashMap(dict, oldDict);
        }
    }

    public String getName() {
        return getPath().getName();
    }

    public int size() {
        return dict.size();
    }

    public int getDiff() {
        int diff = 0;
        for(Map.Entry<String, V> entry: dict.entrySet()) { // Check for new and changed values
            if(!oldDict.containsKey(entry.getKey()) || !oldDict.get(entry.getKey()).equals(entry.getValue())) { // Order of .equals is important here
                diff++;
            }
        }
        for(Map.Entry<String, V> entry: oldDict.entrySet()) { // Check for removed values
            if(!dict.containsKey(entry.getKey())) {
                diff++;
            }
        }
        return diff;
    }

    public int commit() throws DataBaseException {
        int diff = getDiff();
        save();
        copyHashMap(dict, oldDict);
        return diff;
    }

    public int rollback() {
        int diff = getDiff();
        copyHashMap(oldDict, dict);
        return diff;
    }
}
