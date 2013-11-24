package ru.fizteh.fivt.students.kislenko.junit;


import ru.fizteh.fivt.students.kislenko.filemap.CommandUtils;
import ru.fizteh.fivt.students.kislenko.filemap.FatherState;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class JUnitState extends FatherState {
    private Path databasePath;
    private MyTable currentTable;
    private MyTableProvider tables;

    public JUnitState(Path p) {
        databasePath = p;
        MyTableProviderFactory factory = new MyTableProviderFactory();
        tables = factory.create(p.toString());
    }

    public Path getPath() {
        return databasePath;
    }

    public Path getWorkingPath() {
        if (currentTable != null) {
            return databasePath.resolve(currentTable.getName());
        } else {
            return databasePath;
        }
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

    public void setCurrentTable(String name) {
        if (name == null) {
            currentTable = null;
        } else {
            currentTable = tables.getTable(databasePath.resolve(name).toString());
        }
    }

    @Override
    public boolean alright(AtomicReference<Exception> checkingException, AtomicReference<String> message) {
        return CommandUtils.multiTablePutGetRemoveAlright(currentTable, checkingException, message);
    }

    @Override
    public String get(String key, AtomicReference<Exception> exception) {
        return currentTable.get(key);
    }

    @Override
    public void put(String key, String value, AtomicReference<Exception> exception) {
        currentTable.put(key, value);
    }

    @Override
    public void remove(String key, AtomicReference<Exception> exception) {
        currentTable.remove(key);
    }
}
