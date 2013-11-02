package ru.fizteh.fivt.students.kamilTalipov.database;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MultiFileHashTableProvider implements TableProvider {
    public MultiFileHashTableProvider(String databaseDirectory) throws FileNotFoundException,
                                                                        DatabaseException {
        if (databaseDirectory == null) {
            throw new DatabaseException("Database directory path must be not null");
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
    public MultiFileHashTable getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Table name must be not null");
        }

        int tableIndex = indexOfTable(name);
        if (tableIndex != -1) {
            return tables.get(tableIndex);
        }

        return null;
    }

    @Override
    public MultiFileHashTable createTable(String name) throws IllegalArgumentException {
        if (getTable(name) != null) {
            return null;
        }

        MultiFileHashTable newTable;
        try {
            newTable = new MultiFileHashTable(databaseDirectory.getAbsolutePath(), name);
            tables.add(newTable);
        } catch (DatabaseException e) {
            IllegalArgumentException exception = new IllegalArgumentException("Database error");
            exception.addSuppressed(e);
            throw exception;
        } catch (FileNotFoundException e) {
            IllegalArgumentException exception = new IllegalArgumentException("File not found");
            exception.addSuppressed(e);
            throw exception;
        }

        return newTable;
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Table name must be not null");
        }

        int tableIndex = indexOfTable(name);
        if (tableIndex == -1) {
            throw new IllegalStateException("Table '" + name + "' not exist");
        }

        try {
            tables.get(tableIndex).removeTable();
        } catch (DatabaseException e) {
            IllegalArgumentException exception = new IllegalArgumentException("Database error");
            exception.addSuppressed(e);
            throw new IllegalArgumentException();
        }

        tables.remove(tableIndex);
    }

    public void exit() throws DatabaseException {
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
}
