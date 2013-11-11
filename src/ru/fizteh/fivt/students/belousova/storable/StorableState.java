package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.belousova.multifilehashmap.MultiFileShellState;

import java.io.IOException;
import java.text.ParseException;

public class StorableState extends MultiFileShellState {
    private ChangesCountingTableProvider tableProvider;
    private ChangesCountingTable currentTable;

    public StorableState(ChangesCountingTableProvider provider, ChangesCountingTable table) {
        tableProvider = provider;
        currentTable = table;
    }

    @Override
    public boolean getTable(String name) {
        return (tableProvider.getTable(name) != null);
    }

    @Override
    public boolean createTable(String name) {
        return (tableProvider.getTable(name) != null);
    }

    @Override
    public void removeTable(String name) {
        try {
            tableProvider.removeTable(name);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public void setCurrentTable(String name) {
        currentTable = tableProvider.getTable(name);
    }

    @Override
    public void resetCurrentTable() {
        currentTable = null;
    }

    @Override
    public String getFromCurrentTable(String key) {
        Storeable storeable = currentTable.get(key);
        return tableProvider.serialize(currentTable, storeable);
    }

    @Override
    public String putToCurrentTable(String key, String value) {
        try {
            Storeable newValue = tableProvider.deserialize(currentTable, value);
            Storeable oldValue = currentTable.put(key, newValue);
            if (oldValue != null) {
                return tableProvider.serialize(currentTable, oldValue);
            } else {
                return null;
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public String removeFromCurrentTable(String key) {
        Storeable oldValue = currentTable.remove(key);
        if (oldValue != null) {
            return tableProvider.serialize(currentTable, oldValue);
        } else {
            return null;
        }
    }

    @Override
    public String getCurrentTable() {
        if (currentTable == null) {
            return null;
        }
        return currentTable.getName();
    }

    @Override
    public int commitCurrentTable() {
        try {
            return currentTable.commit();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public int sizeOfCurrentTable() {
        return currentTable.size();
    }

    @Override
    public int rollbackCurrentTable() {
        return currentTable.rollback();
    }

    @Override
    public int getChangesCountOfCurrentTable() {
        return currentTable.getChangesCount();
    }
}
