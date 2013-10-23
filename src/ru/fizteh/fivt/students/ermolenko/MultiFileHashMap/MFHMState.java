package ru.fizteh.fivt.students.ermolenko.MultiFileHashMap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;

public class MFHMState {

    private MFHMTableProvider provider;
    private MFHMTable currentTable;
    private int flag;
    //ЗАЧЕМ???
    //private File dataFile;

    public MFHMState(File inFile) throws IOException {

        flag = 0;
        //dataFile = inFile;
        currentTable = null;
        MFHMTableProviderFactory factory = new MFHMTableProviderFactory();
        provider = (MFHMTableProvider) factory.create(inFile.getPath());
    }

    public int getFlag() {

        return flag;
    }

    public void changeFlag() {

        if (0 == flag) {
            flag = 1;
        } else {
            flag = 0;
        }
    }

    /*
    public File getFile() {

        return dataFile;
    }
    */
    public Table createTable(String name) throws IOException {

        return provider.createTable(name);
    }

    public Table getTable(String name) throws IOException {

        return provider.getTable(name);
    }

    public Table getCurrentTable() throws IOException {

        return currentTable;
    }

    public void setCurrentTable(String name) throws IOException {

        currentTable = (MFHMTable) provider.getTable(name);
    }

    public void deleteCurrentTable() throws IOException {

        provider.removeTable(currentTable.getName());
        currentTable = null;
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
