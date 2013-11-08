package ru.fizteh.fivt.students.kamilTalipov.database.core;


import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class HashDatabase implements MultiTableDatabase, TransactionDatabase {
    private final MultiFileHashTableProvider tableProvider;
    private MultiFileHashTable activeTable;

    public HashDatabase(String databaseDirectory) throws IOException, DatabaseException {
        this.tableProvider = new MultiFileHashTableProvider(databaseDirectory);
        activeTable = null;
    }

    @Override
    public boolean createTable(String tableName, List<Class<?>> types) {
        if (tableProvider.getTable(tableName) != null) {
            return false;
        }

        try {
            tableProvider.createTable(tableName, types);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return true;
    }

    @Override
    public boolean dropTable(String tableName) {
        try {
            if (activeTable != null && activeTable.equals(tableName)) {
                activeTable = null;
            }
            tableProvider.removeTable(tableName);
        } catch (IllegalStateException e) {
            return false;
        }

        return true;
    }

    @Override
    public int setActiveTable(String tableName) {
        MultiFileHashTable newActiveTable = tableProvider.getTable(tableName);
        if (newActiveTable != null) {
            int uncommittedChanges = 0;
            if (activeTable != null) {
                uncommittedChanges = activeTable.uncommittedChanges();
            }
            if (uncommittedChanges > 0) {
                return uncommittedChanges;
            }
            activeTable = newActiveTable;
            return 0;
        }

        return -1;
    }

    @Override
    public Storeable put(String key, String stringValue) throws NoTableSelectedException, ParseException {
        if (activeTable == null) {
            throw new NoTableSelectedException("HashDatabase: No table selected");
        }
        Storeable value = null;

        value = tableProvider.deserialize(activeTable, stringValue);

        return activeTable.put(key, value);
    }

    @Override
    public Storeable get(String key) throws NoTableSelectedException {
        if (activeTable == null) {
            throw new NoTableSelectedException("HashDatabase: No table selected");
        }
        return activeTable.get(key);
    }

    @Override
    public Storeable remove(String key) throws NoTableSelectedException {
        if (activeTable == null) {
            throw new NoTableSelectedException("HashDatabase: No table selected");
        }
        return activeTable.remove(key);
    }

    @Override
    public int size() throws NoTableSelectedException {
        if (activeTable == null) {
            throw new NoTableSelectedException("HashDatabase: No table selected");
        }
        return activeTable.size();
    }

    @Override
    public int commit() throws NoTableSelectedException {
        if (activeTable == null) {
            throw new NoTableSelectedException("HashDatabase: No table selected");
        }
        return activeTable.commit();
    }

    @Override
    public int rollback() throws NoTableSelectedException {
        if (activeTable == null) {
            throw new NoTableSelectedException("HashDatabase: No table selected");
        }
        return activeTable.rollback();
    }

    @Override
    public void exit() throws DatabaseException {
        tableProvider.exit();
    }
}
