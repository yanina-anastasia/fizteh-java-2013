package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class MultiFileShellState {
    private TableProvider tableProvider = null;
    private Table currentTable = null;

    public MultiFileShellState(TableProvider tableProvider, Table table) {
        this.tableProvider = tableProvider;
        this.currentTable = table;
    }
    public Table getTable(String name) {
        return tableProvider.getTable(name);
    }
    public Table createTable(String name) {
        return tableProvider.createTable(name);
    }
    public void removeTable(String name) {
        tableProvider.removeTable(name);
    }
    public void setCurrentTable(String name) {
        currentTable = tableProvider.getTable(name);
    }
    public void resetCurrentTable() {
        currentTable = null;
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

    int commitCurrentTable() {
        return currentTable.commit();
    }
}
