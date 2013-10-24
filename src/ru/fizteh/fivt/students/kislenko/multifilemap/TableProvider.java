package ru.fizteh.fivt.students.kislenko.multifilemap;

import java.util.HashMap;
import java.util.Map;

public class TableProvider implements ru.fizteh.fivt.storage.strings.TableProvider {
    private Map<String, Table> tables = new HashMap<String, Table>();

    @Override
    public Table getTable(String name) {
        return tables.get(name);
    }

    @Override
    public Table createTable(String name) {
        Table table = new Table(name);
        tables.put(name, table);
        return table;
    }

    @Override
    public void removeTable(String name) {
        tables.remove(name);
    }
}