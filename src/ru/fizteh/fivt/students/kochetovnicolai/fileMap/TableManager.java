package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class TableManager extends FileManager {

    protected TableMember currentTable = null;
    DistributedTableProvider provider;
    HashMap<String, TableMember> tables;

    public boolean existsTable(String name) {
        try {
            return provider.existsTable(name);
        } catch (RuntimeException e) {
            printMessage(e.getMessage());
            return false;
        }
    }

    public TableManager(File workingDirectory) throws IOException {
        currentPath = workingDirectory;
        if (!currentPath.exists() && !currentPath.mkdir()) {
            throw new IOException("couldn't create working directory");
        }
        tables = new HashMap<>();
        provider = new DistributedTableProvider(currentPath);
    }

    void setCurrentTable(TableMember table) {
        currentTable = table;
    }

    public TableMember getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        if (tables.containsKey(name)) {
            return tables.get(name);
        }
        return createTable(name);
    }

    public TableMember createTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        if (!tables.containsKey(name)) {
            try {
                tables.put(name, provider.createTable(name));
            } catch (RuntimeException e) {
                printMessage(e.getMessage());
            }
        }
        return tables.get(name);
    }

    public boolean removeTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        if (currentTable == tables.get(name)) {
            currentTable = null;
        }
        tables.remove(name);
        try {
            provider.removeTable(name);
        } catch (RuntimeException e) {
            printMessage(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public void setExit() {
        if (currentTable != null) {
            currentTable.commit();
        }
        super.setExit();
    }

    public Table getCurrentTable() {
        return currentTable;
    }

    @Override
    public void printSuggestMessage() {
        outputStream.print(currentPath.getName() + File.separator);
        if (currentTable != null) {
            outputStream.print(currentTable.getName());
        }
        outputStream.print("$ ");
    }
}
