package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.Index;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.HashMap;

public class DatabaseIndex implements Index {
    DatabaseTable indexTable;
    int column;
    String indexName;
    HashMap<Object, String> indexes;
    DatabaseTableProvider provider;

    DatabaseIndex(DatabaseTable table, int column, String name, HashMap<Object, String> indexes) {
        this.indexTable = table;
        this.column = column;
        this.indexName = name;
        this.indexes = indexes;
    }

    public String getName() {
        return indexName;
    }

    public Storeable get(String key) {
        Object myKey = null;
        if (indexTable.getColumnType(column).equals(String.class)) {
            myKey = key;
        } else {
            myKey = DatabaseTableProvider.typesParser(key, indexTable.getColumnType(column));
        }
        if (indexes.get(myKey) == null) {
            return null;
        }
        return indexTable.get(indexes.get(myKey));
    }
}
