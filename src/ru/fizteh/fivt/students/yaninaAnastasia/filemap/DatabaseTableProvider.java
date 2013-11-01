package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DatabaseTableProvider implements TableProvider {
    HashMap<String, DatabaseTable> tables = new HashMap<String, DatabaseTable>();
    public DatabaseTable curTable = null;

    public DatabaseTableProvider(String directory) {
        if (directory == null || directory.isEmpty()) {
            throw new IllegalArgumentException("Error with the property");
        }
        File databaseDirectory = new File(directory);
        for (final File tableFile : databaseDirectory.listFiles()) {
            if (tableFile.isFile()) {
                continue;
            }
            if ((tableFile.getName() == null) || (tableFile.getName().isEmpty())) {
                throw new IllegalArgumentException("Error with the property");
            }
            DatabaseTable table = new DatabaseTable(tableFile.getName());
            tables.put(table.getName(), table);
        }
    }

    public DatabaseTable getTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || (name.isEmpty() || name.trim().isEmpty())) {
            throw new IllegalArgumentException("table's name cannot be null");
        }
        if (name.contains("\\") || name.contains("/") || name.contains(">") || name.contains("<")
                || name.contains("\"") || name.contains(":") || name.contains("?") || name.contains("|")
                || name.startsWith(".") || name.endsWith(".")) {
            throw new RuntimeException("Bad symbols in tablename " + name);
        }

        DatabaseTable table = tables.get(name);

        if (table == null) {
            return table;
        }

        table.putName(name);

        if (curTable != null && curTable.uncommittedChanges > 0) {
            throw new IllegalArgumentException(String.format("%d unsaved changes", curTable.uncommittedChanges));
        }

        curTable = table;
        return table;
    }

    public Table createTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || (name.isEmpty() || name.trim().isEmpty())) {
            throw new IllegalArgumentException("table's name cannot be null");
        }
        if (name.contains("\\") || name.contains("/") || name.contains(">") || name.contains("<")
                || name.contains("\"") || name.contains(":") || name.contains("?") || name.contains("|")
                || name.startsWith(".") || name.endsWith(".")) {
            throw new RuntimeException("Bad symbols in tablename " + name);
        }

        if (tables.containsKey(name)) {
            return null;
        }

        DatabaseTable table = new DatabaseTable(name);
        tables.put(name, table);
        return table;
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null || (name.isEmpty() || name.trim().isEmpty())) {
            throw new IllegalArgumentException("table's name cannot be null");
        }
        if (name.contains("\\") || name.contains("/") || name.contains(">") || name.contains("<")
                || name.contains("\"") || name.contains(":") || name.contains("?") || name.contains("|")
                || name.startsWith(".") || name.endsWith(".")) {
            throw new RuntimeException("Bad symbols in tablename " + name);
        }

        if (!tables.containsKey(name)) {
            throw new IllegalStateException(String.format("%s not exists", name));
        }

        tables.remove(name);
    }
}
