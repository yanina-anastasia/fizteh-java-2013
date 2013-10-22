package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class DatabaseContext {
    private HashMap<String, String> dataBase = null;
    private Path dbRoot;
    private Path activeDir;

    public String remove(String key) throws Exception {
        if (dataBase == null) {
            throw new Exception("no table");
        }
        String removed = dataBase.remove(key);
        return removed;
    }

    public String get(String key) throws Exception {
        if (dataBase == null) {
            throw new Exception("no table");
        }
        return dataBase.get(key);
    }

    public String put(String key, String value) throws Exception {
        if (dataBase == null) {
            throw new Exception("no table");
        }
        String previousValue = dataBase.put(key, value);
        return previousValue;
    }
    
    public void loadTable(String dbName) throws Exception {
        Path dbPath = Paths.get(dbRoot.normalize() + "/" + dbName);
        File dbDir = dbPath.toFile();
        if (!dbDir.exists() || !dbDir.isDirectory()) {
            throw new Exception("tablename not exists");
        }
        dataBase = IOUtility.parseDatabase(dbPath);
        activeDir = dbPath;
    }
    
    public void closeActiveTable() throws Exception {
        if (dataBase != null) {
            IOUtility.writeOut(dataBase, activeDir);
        }
    }
    
    public Path getActiveDir() {
        return activeDir;
    }

    public Path getRootDir() {
        return dbRoot;
    }
    
    public DatabaseContext(Path path) throws Exception {
        dbRoot = path;
        if (dbRoot == null || !dbRoot.toFile().isDirectory()) {
            throw new Exception("fizteh.db.dir did not resolve to a valid directory");
        }
    } 
}
