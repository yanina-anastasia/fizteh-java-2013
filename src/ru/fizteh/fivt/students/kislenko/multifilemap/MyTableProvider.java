package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MyTableProvider implements TableProvider {
    private Map<String, MyTable> tables = new HashMap<String, MyTable>();

    @Override
    public MyTable getTable(String name) {
        if (name == null) {
            File file = new File(name);
            Path path = file.toPath();
            if (!path.getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
                throw new IllegalArgumentException("Incorrect table name.");
            }
        }
        return tables.get(name);
    }

    @Override
    public MyTable createTable(String name) {
        if (name == null) {
            File file = new File(name);
            Path path = file.toPath();
            if (!path.getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
                throw new IllegalArgumentException("Incorrect table name.");
            }
        }
        MyTable table = new MyTable(name);
        if (table.getSize() == -1) {
            return null;
        }
        tables.put(name, table);
        return table;
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            File file = new File(name);
            Path path = file.toPath();
            if (!path.getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
                throw new IllegalArgumentException("Incorrect table name.");
            }
        }
        if (tables.get(name) == null) {
            throw new IllegalStateException("Have no table to remove.");
        }
        tables.remove(name);
    }
}