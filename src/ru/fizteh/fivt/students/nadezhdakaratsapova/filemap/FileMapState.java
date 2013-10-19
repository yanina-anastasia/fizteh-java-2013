package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileMapState {

    private Map<String, String> dataStorage = new HashMap<String, String>();
    private File dataFile;

    public File getDataFile() {
        return dataFile;
    }

    public FileMapState(File file) {
        dataFile = file;
    }

    public void add(String key, String value) {
        dataStorage.put(key, value);
    }

    public Set<String> getKeys() {
        return dataStorage.keySet();
    }

    public String getValue(String key) {
        return dataStorage.get(key);
    }

    public String remove(String key) {
        return dataStorage.remove(key);
    }


}
