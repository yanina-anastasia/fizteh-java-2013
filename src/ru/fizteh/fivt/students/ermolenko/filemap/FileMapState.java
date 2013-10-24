package ru.fizteh.fivt.students.ermolenko.filemap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileMapState {

    //для хранения <key, value>
    private Map<String, String> dataBase;
    private File dataFile;

    public Map<String, String> getDataBase() {

        return dataBase;
    }

    public File getDataFile() {

        return dataFile;
    }

    public FileMapState(File currentFile) {

        dataBase = new HashMap<String, String>();
        dataFile = currentFile;
    }

    public void setDataBase(Map<String, String> map) {

        dataBase = map;
    }
}
