package ru.fizteh.fivt.students.kislenko.storeable;

import ru.fizteh.fivt.students.kislenko.filemap.CommandUtils;
import ru.fizteh.fivt.students.kislenko.filemap.FatherState;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicReference;

public class StoreableState extends FatherState {
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

    @Override
    public boolean alright(AtomicReference<Exception> checkingException, AtomicReference<String> message) {
        return CommandUtils.multiTablePutGetRemoveAlright(currentTable, checkingException, message);
    }

    @Override
    public String get(String key, AtomicReference<Exception> exception) {
        return currentTable.getProvider().serialize(currentTable, currentTable.get(key));
    }

    @Override
    public void put(String key, String value, AtomicReference<Exception> exception) {
        try {
            currentTable.put(key, tables.deserialize(currentTable, value));
        } catch (ParseException e) {
            exception.set(e);
        }
    }

    @Override
    public void remove(String key, AtomicReference<Exception> exception) {
        currentTable.remove(key);
    }
}
