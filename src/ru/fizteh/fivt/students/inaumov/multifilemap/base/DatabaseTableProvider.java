package ru.fizteh.fivt.students.inaumov.multifilemap.base;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapUtils;

import java.util.HashMap;
import java.io.File;
import java.io.IOException;

public class DatabaseTableProvider implements TableProvider {
    HashMap<String, MultiFileTable> tables = new HashMap<String, MultiFileTable>();

    private String dataBaseDirectoryPath;
    private MultiFileTable currentTable = null;

    public DatabaseTableProvider(String dataBaseDirectoryPath) throws IllegalStateException {
        this.dataBaseDirectoryPath = dataBaseDirectoryPath;

        File dataBaseDirectory = new File(dataBaseDirectoryPath);
        //System.out.println(dataBaseDirectory.getAbsolutePath());
        for (final File tableFile: dataBaseDirectory.listFiles()) {
            if (tableFile.isFile()) {
                continue;
            }

            MultiFileTable table;
            try {
                table = new MultiFileTable(dataBaseDirectoryPath, tableFile.getName());
            } catch (IOException exception) {
                throw new IllegalStateException(exception.getMessage());
            }

            tables.put(table.getName(), table);
        }
    }

    public Table getTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("table name can't be null");
        }

        MultiFileTable table = tables.get(name);
        if (table == null) {
            throw new IllegalStateException(name + " not exists");
        }

        if (currentTable != null && currentTable.getUnsavedChangesNumber() > 0) {
            throw new IllegalStateException(currentTable.getUnsavedChangesNumber() + " unsaved changes");
        }

        currentTable = table;

        return table;
    }

    public Table createTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("table name can't be null");
        }
        if (tables.containsKey(name)) {
            throw new IllegalStateException(name + " exists");
        }

        MultiFileTable table;
        try {
            table = new MultiFileTable(dataBaseDirectoryPath, name);
        } catch (IOException exception) {
            throw new IllegalStateException(exception.getMessage());
        }

        tables.put(name, table);

        return table;
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("table name can't be null");
        }
        if (!tables.containsKey(name)) {
            throw new IllegalStateException(name + " not exists");
        }

        tables.remove(name);

        File tableFile = new File(dataBaseDirectoryPath, name);
        MultiFileMapUtils.deleteFile(tableFile);
    }
}
