package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.students.kochetovnicolai.shell.Manager;

import java.util.HashMap;

public class TableManager extends Manager {

    protected TableMember currentTable = null;
    DistributedTableProvider provider;
    HashMap<String, TableMember> tables;

    public boolean existsTable(String name) {
        if (!tables.containsKey(name)) {
            try {
                createTable(name);
            } catch (IllegalArgumentException e) {
                printMessage(e.getMessage());
                return false;
            }
        }
        return tables.containsKey(name);
    }

    public TableManager(DistributedTableProvider provider) {
        this.provider = provider;
        tables = new HashMap<>();
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
                if (tables.get(name) == null) {
                   tables.put(name, provider.getTable(name)); 
                }
            } catch (IllegalArgumentException e) {
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
        } catch (IllegalArgumentException e) {
            printMessage(e.getMessage());
            return false;
        }
        return true;
    }

    public TableMember getCurrentTable() {
        return currentTable;
    }

    @Override
    public void printSuggestMessage() {
        if (currentTable != null) {
            outputStream.print(currentTable.getName());
        }
        outputStream.print("$ ");
    }
}
