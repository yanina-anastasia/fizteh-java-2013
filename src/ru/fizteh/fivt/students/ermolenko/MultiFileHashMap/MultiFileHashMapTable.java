package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapTable implements Table {

    private Map<String, String> dataBase;
    public File dataFile;

    public MultiFileHashMapTable(File currentFile) throws IOException {

        dataBase = new HashMap<String, String>();
        dataFile = currentFile;
        MultiFileHashMapUtils.read(currentFile, dataBase);
    }

    public Map<String, String> getDataBase() {
        return dataBase;
    }

    public File getDataFile() {
        return dataFile;
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
        return dataBase.get(key);
    }

    @Override
    public String put(String key, String value) {

        if (key == null || value == null) {
            throw new IllegalArgumentException("Incorrect key/value to put.");
        }
        return dataBase.put(key, value);
    }

    @Override
    public String remove(String key) {

        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }
        return dataBase.remove(key);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int commit() throws IOException {

        MultiFileHashMapUtils.write(dataFile, dataBase);
        return 0;
    }

    @Override
    public int rollback() {
        return 0;
    }
}