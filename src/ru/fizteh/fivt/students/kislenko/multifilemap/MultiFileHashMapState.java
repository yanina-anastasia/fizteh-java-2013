package ru.fizteh.fivt.students.kislenko.multifilemap;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class MultiFileHashMapState {
    private Map<String, String> currentStorage;
    private Map<String, TableController> databases;
    private Path databasePath;
    private String workingTableName;

    public MultiFileHashMapState(Path p) {
        currentStorage = new TreeMap<String, String>();
        databases = new TreeMap<String, TableController>();
        databasePath = p;
        workingTableName = "";
    }

    public Path getPath() {
        return databasePath;
    }

    public void deleteTable(String tableName) {
        databases.remove(tableName);
    }

    public void createTable(String tableName) {
        TableController tc = new TableController(databasePath, tableName);
        databases.put(tableName, tc);
    }

    public String getWorkingTableName() {
        return workingTableName;
    }

    public void setWorkingPath(String tableName) {
        workingTableName = tableName;
    }

    public TableController getCurrentTableController() {
        if (workingTableName.equals("")) {
            return null;
        } else {
            return databases.get(workingTableName);
        }
    }

    public Path getWorkingPath() {
        return databasePath.resolve(workingTableName);
    }

    public void putValue(String key, String value) {
        currentStorage.put(key, value);
    }

    public String getValue(String key) {
        return currentStorage.get(key);
    }

    public void delValue(String key) {
        currentStorage.remove(key);
    }

    public boolean hasKey(String key) {
        return currentStorage.containsKey(key);
    }

    public Map<String, String> getMap() {
        return currentStorage;
    }
}