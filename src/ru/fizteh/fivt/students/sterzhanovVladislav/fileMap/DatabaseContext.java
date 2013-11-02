package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.Closeable;
import java.io.IOException;

public class DatabaseContext implements Closeable {
    private FileMapProvider provider = null;
    private FileMap activeMap = null;

    public String remove(String key) throws Exception {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        return activeMap.remove(key);
    }

    public String get(String key) throws Exception {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        return activeMap.get(key);
    }

    public String put(String key, String value) throws Exception {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        return activeMap.put(key, value);
    }
    
    public int commit() {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        return activeMap.commit();
    }
    
    public int rollback() {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        return activeMap.rollback();
    }
    
    public int getActiveSize() {
        if (activeMap == null) {
            throw new IllegalStateException("no table");
        }
        return activeMap.size();
    }
    
    public void loadTable(String dbName) throws IllegalStateException, IOException {
        if (activeMap != null && activeMap.isDirty()) {
            throw new IllegalStateException(activeMap.getDiffSize() + " unsaved changes");
        }
        FileMap newMap = provider.getTable(dbName);
        if (newMap == null) {
            throw new IllegalStateException(dbName + " not exists");
        }
        closeActiveTable();
        activeMap = newMap;
    }
    
    public void createTable(String dbName) throws IllegalStateException {
        FileMap newMap = provider.createTable(dbName);
        if (newMap == null) {
            throw new IllegalStateException(dbName + " exists");
        }
    }
    
    public void removeTable(String dbName) throws IllegalStateException {
        provider.removeTable(dbName);
        if (!(activeMap == null) && dbName.equals(activeMap.getName())) {
            activeMap = null;
        }
    }
    
    public void closeActiveTable() throws IOException {
        if (activeMap != null) {
            activeMap.writeOut(provider.getRootDir());
            activeMap = null;
        }
    }
    
    public DatabaseContext(String path) throws IllegalStateException {
        provider = new FileMapProvider(path);
    } 
    
    public void close() throws IOException {
        closeActiveTable();
    }
}
