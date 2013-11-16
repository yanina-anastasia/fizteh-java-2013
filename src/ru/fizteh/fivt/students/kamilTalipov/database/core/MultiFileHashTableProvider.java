package ru.fizteh.fivt.students.kamilTalipov.database.core;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kamilTalipov.database.utils.FileUtils;
import ru.fizteh.fivt.students.kamilTalipov.database.utils.JsonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MultiFileHashTableProvider implements TableProvider {
    private final File databaseDirectory;
    private final ArrayList<MultiFileHashTable> tables;

    public MultiFileHashTableProvider(String databaseDirectory) throws IOException,
            DatabaseException {
        if (databaseDirectory == null) {
            throw new IllegalArgumentException("Database directory path must be not null");
        }

        try {
            this.databaseDirectory = FileUtils.makeDir(databaseDirectory);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("File: " + databaseDirectory + " not a directory");
        }

        tables = new ArrayList<>();
        loadTables();
    }

    @Override
    public MultiFileHashTable getTable(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name must be not null");
        }
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name must be not empty");
        }
        if (isContainIncorrectSymbols(name)) {
            throw new IllegalArgumentException("Table name must be correct file name");
        }

        int tableIndex = indexOfTable(name);
        if (tableIndex != -1) {
            return tables.get(tableIndex);
        }

        return null;
    }


    @Override
    public MultiFileHashTable createTable(String name, List<Class<?>> columnTypes) throws
                                                                        IllegalArgumentException, IOException {
        if (getTable(name) != null) {
            return null;
        }

        MultiFileHashTable newTable;
        try {
            newTable = new MultiFileHashTable(databaseDirectory.getAbsolutePath(), name, this, columnTypes);
            tables.add(newTable);
        } catch (FileNotFoundException e) {
            IOException exception = new IOException("File not found");
            exception.addSuppressed(e);
            throw exception;
        } catch (DatabaseException e) {
            IllegalArgumentException exception = new IllegalArgumentException("Database error");
            exception.addSuppressed(e);
            throw exception;
        }

        return newTable;
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("Table name must be not null");
        }
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name must be not empty");
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

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return JsonUtils.deserialize(value, this, table);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return JsonUtils.serialize(value, table);
    }

    @Override
    public Storeable createFor(Table table) {
        return new TableRow(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException,
                                                                    IndexOutOfBoundsException {
        return new TableRow(table, values);
    }

    public void exit() throws DatabaseException {
        for (MultiFileHashTable table : tables) {
            table.exit();
        }
    }

    public void remove() {
        FileUtils.remove(databaseDirectory);
    }

    private int indexOfTable(String tableName) {
        for (int i = 0; i < tables.size(); ++i) {
            if (tables.get(i).getName().equals(tableName)) {
                return i;
            }
        }

        return -1;
    }

    private void loadTables() throws DatabaseException, IOException {
        File[] innerFiles = databaseDirectory.listFiles();
        for (File file : innerFiles) {
            if (file.isDirectory()) {
                tables.add(new MultiFileHashTable(databaseDirectory.getAbsolutePath(), file.getName(), this));
            }
        }
    }

    private boolean isContainIncorrectSymbols(String tableName) {
        return tableName.contains("\\") || tableName.contains("/")
                || tableName.contains(":") || tableName.contains("*")
                || tableName.contains("?") || tableName.contains("\"")
                || tableName.contains("<") || tableName.contains(">")
                || tableName.contains("|");
    }
}
