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
        provider = (MultiFileHashMapTableProvider) factory.create(inFile.getPath());
    }

    public void changeCurrentTable(Map<String, String> inMap, File inFile) {

        currentTable.setDataBase(inMap);
        currentTable.setDataFile(inFile);
    }

    public Table createTable(String name) throws IOException {

        Table tmp = provider.createTable(name);
        return tmp;
    }

    public Table getTable(String name) throws IOException {

        return provider.getTable(name);
    }

    public Table getCurrentTable() throws IOException {

        return currentTable;
    }

    public void setCurrentTable(String name) throws IOException {

        currentTable = (MultiFileHashMapTable) provider.getTable(name);
    }

    public void deleteTable(String name) throws IOException {

        provider.removeTable(name);
    }

    public String putToCurrentTable(String key, String value) {

        return currentTable.put(key, value);
    }

    public String getFromCurrentTable(String key) {

        return currentTable.get(key);
    }

    public String removeFromCurrentTable(String key) {

        return currentTable.remove(key);
    }
}
