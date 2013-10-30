package ru.fizteh.fivt.students.kamilTalipov.database;


import java.io.FileNotFoundException;

public class HashDatabase implements MultiTableDatabase, TransactionDatabase {
    public HashDatabase(String databaseDirectory) throws FileNotFoundException, DatabaseException {
        this.tableProvider = new MultiFileHashTableProvider(databaseDirectory);
        activeTable = null;
    }

    @Override
    public boolean createTable(String tableName) {
        if (tableProvider.getTable(tableName) != null) {
            return false;
        }

        try {
            tableProvider.createTable(tableName);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

        return true;
    }

    @Override
    public boolean dropTable(String tableName) {
        try {
            if (activeTable.equals(tableName)) {
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
                uncommittedChanges = activeTable.allUncommittedChanges();
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
    public String put(String key, String value) throws NoTableSelectedException {
        if (activeTable == null) {
            throw new NoTableSelectedException("HashDatabase: No table selected");
        }
        return activeTable.put(key, value);
    }

    @Override
    public String get(String key) throws NoTableSelectedException {
        if (activeTable == null) {
            throw new NoTableSelectedException("HashDatabase: No table selected");
        }
        return activeTable.get(key);
    }

    @Override
    public String remove(String key) throws NoTableSelectedException {
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
    public void exit() {
        tableProvider.exit();
    }

    private final MultiFileHashTableProvider tableProvider;
    private MultiFileHashTable activeTable;
}
