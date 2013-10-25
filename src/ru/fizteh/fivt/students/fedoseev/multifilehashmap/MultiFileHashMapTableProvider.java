package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapTableProvider implements TableProvider {
    private File curDir;
    private Map<String, MultiFileHashMapTable> tables = new HashMap<String, MultiFileHashMapTable>();

    public MultiFileHashMapTableProvider(String dir) {
        curDir = new File(dir);
    }

    @Override
    public MultiFileHashMapTable getTable(String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("GETTABLE ERROR: incorrect table name");
        }

        return tables.get(tableName);
    }

    @Override
    public MultiFileHashMapTable createTable(String tableName) {
        if (tables.containsKey(tableName)) {
            return null;
        }

        MultiFileHashMapTable table = new MultiFileHashMapTable(tableName);

        tables.put(tableName, table);

        return table;
    }

    @Override
    public void removeTable(String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("REMOVETABLE ERROR: incorrect table name");
        }
        if (!tables.containsKey(curDir.toString() + "\\" + tableName)) {
            throw new IllegalStateException("REMOVETABLE ERROR: not existing table");
        }

        tables.remove(curDir.toString() + "\\" + tableName);
    }
}