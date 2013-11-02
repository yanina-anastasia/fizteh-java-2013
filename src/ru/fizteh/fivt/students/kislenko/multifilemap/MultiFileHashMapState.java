package ru.fizteh.fivt.students.kislenko.multifilemap;

import java.nio.file.Path;

public class MultiFileHashMapState {
    private Path databasePath;
    private MyTable currentTable;
    private MyTableProvider tables;

    public MultiFileHashMapState(Path p) {
        databasePath = p;
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

    public MyTable getCurrentTable() {
        return currentTable;
    }

    public Path getWorkingPath() {
        if (currentTable != null) {
            return databasePath.resolve(currentTable.getName());
        } else {
            return databasePath;
        }
    }

    public void setCurrentTable(String name) {
        currentTable = tables.getTable(databasePath.resolve(name).toString());
    }
}