package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.storage.strings.Table;

public class TableState {
    protected Table currentTable = null;

    public TableState(){}

    public TableState(Table table) {
        currentTable = table;
    }

    public String getCurrentTable() {
        if (currentTable == null) {
            return null;
        }
        return currentTable.getName();
    }

    public String getFromCurrentTable(String key) {
        return currentTable.get(key);
    }

    public String putToCurrentTable(String key, String value) {
        return currentTable.put(key, value);
    }

    public String removeFromCurrentTable(String key) {
        return currentTable.remove(key);
    }

    public int commitCurrentTable() {
        return currentTable.commit();
    }
}
