package ru.fizteh.fivt.students.kislenko.multifilemap;

import java.nio.file.Path;

public class MultiFileHashMapState {
    private Path databasePath;
    private String workingTableName;
    private MyTable currentTable;
    private MyTableProvider tables;

    public MultiFileHashMapState(Path p) {
        databasePath = p;
        workingTableName = "";
        MyTableProviderFactory factory = new MyTableProviderFactory();
        tables = factory.create(p.toString());
    }

    public Path getPath() {
        return databasePath;
    }

    public void deleteTable(String tableName) {
        tables.removeTable(tableName);
    }

    public void createTable(String tableName) {
        tables.createTable(databasePath.resolve(tableName).toString());
    }

    public String getWorkingTableName() {
        return workingTableName;
    }

    public void setWorkingPath(String tableName) {
        workingTableName = tableName;
    }

    public MyTable getCurrentTable() {
        if (workingTableName.equals("")) {
            return null;
        } else {
            return currentTable;
        }
    }

    public Path getWorkingPath() {
        return databasePath.resolve(workingTableName);
    }

    public void setCurrentTable(String name) {
        currentTable = tables.getTable(databasePath.resolve(name).toString());
    }
}