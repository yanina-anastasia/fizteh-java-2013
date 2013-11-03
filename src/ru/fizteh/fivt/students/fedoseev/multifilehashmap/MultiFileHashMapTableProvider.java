package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapTableProvider implements TableProvider {
    private final String NAME_FORMAT = "[а-яА-яa-zA-Z0-9]+";

    private Map<String, MultiFileHashMapTable> databaseTables = new HashMap<String, MultiFileHashMapTable>();

    @Override
    public MultiFileHashMapTable getTable(String tableName) {
        if (tableName == null || !new File(tableName).toPath().getFileName().toString().matches(NAME_FORMAT)) {
            throw new IllegalArgumentException("GETTABLE ERROR: incorrect table name");
        }

        return databaseTables.get(tableName);
    }

    @Override
    public MultiFileHashMapTable createTable(String tableName) {
        if (databaseTables.containsKey(tableName)) {
            return null;
        }
        if (tableName == null || !new File(tableName).toPath().getFileName().toString().matches(NAME_FORMAT)) {
            throw new IllegalArgumentException("CREATETABLE ERROR: incorrect table name");
        }

        MultiFileHashMapTable table = new MultiFileHashMapTable(tableName);

        databaseTables.put(tableName, table);

        return table;
    }

    @Override
    public void removeTable(String tableName) {
        if (tableName == null || !new File(tableName).toPath().getFileName().toString().matches(NAME_FORMAT)) {
            throw new IllegalArgumentException("REMOVETABLE ERROR: incorrect table name");
        }
        if (!databaseTables.containsKey(tableName)) {
            throw new IllegalStateException("REMOVETABLE ERROR: not existing table");
        }

        databaseTables.remove(tableName);
    }
}