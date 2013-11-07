package ru.fizteh.fivt.students.kislenko.storeable;

import java.io.IOException;
import java.nio.file.Path;

public class StoreableState {
    private Path databasePath;
    private MyTable currentTable;
    private MyTableProvider tables;

    public StoreableState(Path p) throws IOException {
        databasePath = p;
        MyTableProviderFactory factory = new MyTableProviderFactory();
        tables = factory.create(p.toString());
    }

    public Path getPath() {
        return databasePath;
    }

    public void deleteTable(String tableName) throws IOException {
        tables.removeTable(tableName);
    }

    public void createTable(String tableName) throws IOException, ClassNotFoundException {
        String temp = databasePath.resolve(tableName).toString();
        tables.createTable(temp, Utils.readColumnTypes(temp));
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
        if (name == null) {
            currentTable = null;
        } else {
            currentTable = tables.getTable(databasePath.resolve(name).toString());
        }
    }
}
