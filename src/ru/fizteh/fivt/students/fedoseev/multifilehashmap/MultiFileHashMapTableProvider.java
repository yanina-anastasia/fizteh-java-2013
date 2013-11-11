package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapTableProvider implements TableProvider {
    private final String NAME_FORMAT = "[а-яА-яa-zA-Z0-9]+";

    private Map<String, MultiFileHashMapTable> databaseTables = new HashMap<String, MultiFileHashMapTable>();

    @Override
    public MultiFileHashMapTable getTable(String tableName) {
        checkTableName(tableName);

        return databaseTables.get(tableName);
    }

    @Override
    public MultiFileHashMapTable createTable(String tableName) {
        if (databaseTables.containsKey(tableName)) {
            return null;
        }
        checkTableName(tableName);

        MultiFileHashMapTable table = new MultiFileHashMapTable(tableName);

        databaseTables.put(tableName, table);

        return table;
    }

    @Override
    public void removeTable(String tableName) {
        checkTableName(tableName);

        if (!databaseTables.containsKey(tableName)) {
            throw new IllegalStateException("REMOVE TABLE ERROR: not existing table");
        }

        databaseTables.remove(tableName);
    }

    private void checkTableName(String tableName) throws IllegalArgumentException {
        if (tableName == null || !new File(tableName).toPath().getFileName().toString().matches(NAME_FORMAT)) {
            throw new IllegalArgumentException("GET | CREATE | REMOVE TABLE ERROR: incorrect table name");
        }
    }

    public Map<String, MultiFileHashMapTable> getDatabaseTables() {
        return databaseTables;
    }
}