package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;


import java.io.File;


public class FileMapState {

    public DataTable dataStorage;

    private File dataFile;

    public FileMapState(File file) {
        dataFile = file;
        dataStorage = new DataTable(file.getName());
    }

    public File getDataFile() {
        return dataFile;
    }
}
