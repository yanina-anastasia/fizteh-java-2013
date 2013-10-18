package ru.fizteh.fivt.students.ermolenko.filemap;

import ru.fizteh.fivt.students.ermolenko.shell.State;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileMapState extends State {

    //для хранения <key, value>
    public Map<String, String> dataBase;
    public File dataFile;

    public Map<String, String> getDataBase() {
        return dataBase;
    }

    public File getDataFile() {
        return dataFile;
    }

    private Path path;

    public Path getPath() {
        return path;
    }

    public void setPath(Path inPath) {
        path = inPath;
    }

    public FileMapState(File currentFile) {
        dataBase = new HashMap<String, String>();
        dataFile = currentFile;
    }
}
