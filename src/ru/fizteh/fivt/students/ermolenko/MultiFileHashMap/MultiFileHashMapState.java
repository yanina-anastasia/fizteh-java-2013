package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MultiFileHashMapState {

    private MultiFileHashMapTableProvider provider;
    private MultiFileHashMapTable currentTable;

    public MultiFileHashMapState(File inFile) throws IOException {

        currentTable = null;
        MultiFileHashMapTableProviderFactory factory = new MultiFileHashMapTableProviderFactory();
        provider = factory.create(inFile.getPath());
    }

    public boolean checkChangesTable(String name) {

        return currentTable.checkChangesBase(name);
    }

    public void changeCurrentTable(Map<String, String> inMap, File inFile) {

        currentTable.setDataBase(inMap);
        currentTable.setDataFile(inFile);
    }

    public Table createTable(String name) throws IOException {

        return provider.createTable(name);
    }

    public Table getTable(String name) throws IOException {

        return provider.getTable(name);
    }

    public Table getCurrentTable() throws IOException {

        return currentTable;
    }

    public int getChangesBaseSize() {

        return currentTable.getChangesBaseSize();
    }

    public String getFromChangesBase(String key) {

        return currentTable.getFromChangesBase(key);
    }

    public void putToChangesBase(String key, String value) {

        currentTable.putToChangesBase(key, value);
    }

    public void removeFromChangesBase(String key) {

        currentTable.removeFromChangesBase(key);
    }

    public void setCurrentTable(String name) throws IOException {

        currentTable = (MultiFileHashMapTable) provider.getTable(name);
    }

    public void deleteTable(String name) throws IOException {

        provider.removeTable(name);
        currentTable = null;
    }

    public String getFromCurrentTable(String key) {

        return currentTable.get(key);
    }
}

