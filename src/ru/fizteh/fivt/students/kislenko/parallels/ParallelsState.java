package ru.fizteh.fivt.students.kislenko.parallels;

import java.io.IOException;
import java.nio.file.Path;

public class ParallelsState {
    private Path databasePath;
    private MyTableProvider provider;

    private ThreadLocal<MyTable> currentTable = new ThreadLocal<MyTable>() {
        @Override
        public MyTable initialValue() {
            return null;
        }
    };

    public ParallelsState(Path p) throws IOException {
        databasePath = p;
        MyTableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(p.toString());
    }

    public Path getPath() {
        return databasePath;
    }

    public void deleteTable(String tableName) throws IOException {
        provider.removeTable(tableName);
    }

    public void createTable(String tableName) throws IOException, ClassNotFoundException {
        String temp = databasePath.resolve(tableName).toString();
        provider.createTable(temp, Utils.readColumnTypes(temp));
    }

    public MyTable getCurrentTable() {
        return currentTable.get();
    }

    public Path getWorkingPath() {
        if (currentTable.get() != null) {
            return databasePath.resolve(currentTable.get().getName());
        } else {
            return databasePath;
        }
    }

    public void setCurrentTable(String name) {
        if (name == null) {
            currentTable.set(null);
        } else {
            currentTable.set(provider.getTable(databasePath.resolve(name).toString()));
        }
    }
}
