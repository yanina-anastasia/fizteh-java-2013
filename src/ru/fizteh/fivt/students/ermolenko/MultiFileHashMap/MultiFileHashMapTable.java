package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiFileHashMapTable implements Table {

    private Map<String, String> dataBase;
    private Map<String, String> changesBase;
    private File dataFile;
    private int sizeTable;

    public MultiFileHashMapTable(File currentFile) {

        dataBase = new HashMap<String, String>();
        changesBase = new HashMap<String, String>();
        dataFile = currentFile;
    }

    public int getChangesBaseSize() {

        return changesBase.size();
    }

    public Map<String, String> getDataBase() {

        return dataBase;
    }

    public File getDataFile() {

        return dataFile;
    }

    public void changeCurrentTable(Map<String, String> inMap, File inFile) {

        dataBase = inMap;
        dataFile = inFile;
        sizeTable = dataBase.size();
    }

    @Override
    public String getName() {

        return dataFile.getName();
    }

    @Override
    public String get(String key) {

        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to get.");
        }
        String newKey = key.trim();
        if (newKey.isEmpty()) {
            throw new IllegalArgumentException("Incorrect key to get");
        }
        String returnValue;
        if (changesBase.containsKey(key)) {
            if (changesBase.get(key) == null) {
                returnValue = null;
            } else {
                returnValue = changesBase.get(key);
            }
        } else {
            if (dataBase.containsKey(key)) {
                returnValue = dataBase.get(key);
            } else {
                returnValue = null;
            }
        }

        return returnValue;
    }

    @Override
    public String put(String key, String value) {

        if (key == null || value == null) {
            throw new IllegalArgumentException("Incorrect key or value to put.");
        }

        String newKey = key.trim();
        String newValue = value.trim();
        if (newKey.isEmpty() || newValue.isEmpty()) {
            throw new IllegalArgumentException("Incorrect key or value to put");
        }

        String returnValue;

        if (changesBase.containsKey(key)) {
            if (changesBase.get(key) == null) {
                if (dataBase.get(key).equals(value)) {
                    changesBase.remove(key);
                    ++sizeTable;
                    returnValue = null;
                } else {
                    changesBase.put(key, value);
                    returnValue = null;
                    ++sizeTable;
                }
            } else {
                if (changesBase.get(key).equals(value)) {
                    returnValue = changesBase.get(key);
                } else {
                    returnValue = changesBase.get(key);
                    changesBase.put(key, value);
                }
            }
        } else {
            if (dataBase.get(key) == null) {
                ++sizeTable;
                returnValue = null;
                changesBase.put(key, value);
            } else {
                if (dataBase.get(key).equals(value)) {
                    returnValue = dataBase.get(key);
                } else {
                    returnValue = dataBase.get(key);
                    changesBase.put(key, value);
                }
            }
        }

        return returnValue;
    }

    @Override
    public String remove(String key) {

        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }

        String newKey = key.trim();
        if (newKey.isEmpty()) {
            throw new IllegalArgumentException("Incorrect key to remove");
        }

        String returnValue;
        if (changesBase.containsKey(key)) {
            if (changesBase.get(key) == null) {
                returnValue = null;
            } else {
                returnValue = changesBase.get(key);
                --sizeTable;
                changesBase.remove(key);
            }
        } else {
            if (dataBase.containsKey(key)) {
                returnValue = dataBase.get(key);
                --sizeTable;
                changesBase.put(key, null);
            } else {
                returnValue = null;
            }
        }
        return returnValue;
    }

    @Override
    public int size() {

        return sizeTable;
    }

    @Override
    public int commit() {

        int size = changesBase.size();
        try {
            if (size != 0) {
                Set<Map.Entry<String, String>> set = changesBase.entrySet();
                for (Map.Entry<String, String> pair : set) {
                    pair.getKey();
                    if (pair.getValue() == null) {
                        dataBase.remove(pair.getKey());
                    } else {
                        dataBase.put(pair.getKey(), pair.getValue());
                    }
                }
                MultiFileHashMapUtils.write(dataFile, dataBase);
            }
        } catch (IOException e) {
            System.err.println(e);
        }

        changesBase.clear();
        sizeTable = dataBase.size();
        return size;
    }

    @Override
    public int rollback() {

        int size = changesBase.size();
        changesBase.clear();
        sizeTable = dataBase.size();
        return size;
    }
}