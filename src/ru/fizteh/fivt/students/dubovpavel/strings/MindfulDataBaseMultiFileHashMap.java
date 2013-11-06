package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DataBaseMultiFileHashMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MindfulDataBaseMultiFileHashMap extends DataBaseMultiFileHashMap {
    private HashMap<String, String> oldDict;
    MindfulDataBaseMultiFileHashMap(File path) {
        super(path);
        oldDict = new HashMap<String, String>();
    }

    @Override
    public void open() throws DataBaseException {
        try {
            super.open();
        } finally {
            oldDict = (HashMap<String, String>)dict.clone();
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
        for(Map.Entry<String, String> entry: dict.entrySet()) { // Check for new and changed values
            if(!oldDict.containsKey(entry.getKey()) || !oldDict.get(entry.getKey()).equals(entry.getValue())) {
                diff++;
            }
        }
        for(Map.Entry<String, String> entry: oldDict.entrySet()) { // Check for removed values
            if(!dict.containsKey(entry.getKey())) {
                diff++;
            }
        }
        return diff;
    }

    public int commit() throws DataBaseException {
        int diff = getDiff();
        save();
        oldDict = (HashMap<String, String>)dict.clone(); // Hope it is ok
        return diff;
    }

    public int rollback() {
        int diff = getDiff();
        dict = (HashMap<String, String>)oldDict.clone();
        return diff;
    }
}
