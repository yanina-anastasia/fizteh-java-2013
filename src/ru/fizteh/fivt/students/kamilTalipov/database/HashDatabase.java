package ru.fizteh.fivt.students.kamilTalipov.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class HashDatabase implements MultiTableDatabase {
    public HashDatabase(String databaseDirectory) throws FileNotFoundException, DatabaseException {
        if (databaseDirectory == null) {
            throw new DatabaseException("You should enter property fizteh.db.dir");
        }

        try {
            this.databaseDirectory = FileUtils.makeDir(databaseDirectory);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException("File: " + databaseDirectory + " not a directory");
        }

        tables = new ArrayList<MultiFileHashTable>();
        loadTables();
    }

    @Override
    public boolean createTable(String tableName) {
        int tableIndex = indexOfTable(tableName);
        if (tableIndex != -1) {
            return false;
        }

        try {
            tables.add(new MultiFileHashTable(databaseDirectory.getAbsolutePath(), tableName));
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }

        return true;
    }

    @Override
    public boolean dropTable(String tableName) {
        int tableIndex = indexOfTable(tableName);
        if (tableIndex != -1) {
            try {
                tables.get(tableIndex).removeTable();
            } catch (DatabaseException e) {
                System.err.println(e.getMessage());
            }

            if (tableIndex == currentTable) {
                currentTable = -1;
            }
            tables.remove(tableIndex);
            return true;
        }

        return false;
    }

    @Override
    public int setActiveTable(String tableName) {
        int tableIndex = indexOfTable(tableName);
        if (tableIndex != -1) {
            currentTable = tableIndex;
            return 0;
        }

        return -1;
    }

    @Override
    public String put(String key, String value) throws NoTableSelectedException {
        if (currentTable == -1) {
            throw new NoTableSelectedException("HashDatabase: No table selected");
        }
        return tables.get(currentTable).put(key, value);
    }

    @Override
    public String get(String key) throws NoTableSelectedException {
        if (currentTable == -1) {
            throw new NoTableSelectedException("HashDatabase: No table selected");
        }
        return tables.get(currentTable).get(key);
    }

    @Override
    public String remove(String key) throws NoTableSelectedException {
        if (currentTable == -1) {
            throw new NoTableSelectedException("HashDatabase: No table selected");
        }
        return tables.get(currentTable).remove(key);
    }

    @Override
    public void exit() {
        for (MultiFileHashTable table : tables) {
            table.exit();
        }
    }

    private int indexOfTable(String tableName) {
        for (int i = 0; i < tables.size(); ++i) {
            if (tables.get(i).getName().equals(tableName)) {
                return i;
            }
        }

        return -1;
    }

    private void loadTables() throws DatabaseException, FileNotFoundException {
        File[] innerFiles = databaseDirectory.listFiles();
        for (File file : innerFiles) {
            if (file.isDirectory()) {
                tables.add(new MultiFileHashTable(databaseDirectory.getAbsolutePath(), file.getName()));
            }
        }
    }

    private final File databaseDirectory;
    private final ArrayList<MultiFileHashTable> tables;
    private int currentTable = -1;
}
