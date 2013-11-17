package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;
import java.util.List;

public class StorableShellState {
    public ChangesCountingTable currentTable = null;
    public StorableTableProvider tableProvider = null;

    public StorableShellState(StorableTable table, StorableTableProvider provider) {
        currentTable = table;
        tableProvider = provider;
    }

    public String getCurrentTable() {
        if (currentTable == null) {
            return null;
        }
        return currentTable.getName();
    }

    public Storeable getFromCurrentTable(String key) {
        return currentTable.get(key);
    }

    public Storeable putToCurrentTable(String key, Storeable value) {
        return currentTable.put(key, value);
    }

    public Storeable removeFromCurrentTable(String key) {
        return currentTable.remove(key);
    }

    public int commitCurrentTable() throws IOException {
        return currentTable.commit();
    }

    public int sizeOfCurrentTable() {
        return currentTable.size();
    }

    public int rollbackCurrentTable() {
        return currentTable.rollback();
    }

    public int getChangesCountOfCurrentTable() {
        return currentTable.getChangesCount();
    }

    public ChangesCountingTable getTable(String name) {
        return tableProvider.getTable(name);
    }

    public ChangesCountingTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        return tableProvider.createTable(name, columnTypes);
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
}
