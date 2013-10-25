package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileMapState {

    public DataTable dataStorage = new DataTable();

    private File dataFile;

    public File getDataFile() {
        return dataFile;
    }

    public FileMapState(File file) {
        dataFile = file;
    }


}
