package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Manager;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

public class TableManager extends Manager {

    protected TableMember currentTable = null;
    DistributedTableProvider provider;
    HashMap<String, TableMember> tables;

    public boolean existsTable(String name) {
        if (!tables.containsKey(name)) {
            try {
                return provider.existsTable(name);
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
        if (!tables.containsKey(name)) {
            TableMember table = provider.getTable(name);
            if (table != null) {
                tables.put(name, table);
            }
        }
        return tables.get(name);
    }

    public TableMember createTable(String name, List<Class<?>> columnTypes) throws IllegalArgumentException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        if (!tables.containsKey(name)) {
            try {
                tables.put(name, provider.createTable(name, columnTypes));
                if (tables.get(name) == null) {
                   tables.put(name, provider.getTable(name));
                }
            } catch (IllegalArgumentException e) {
                printMessage(e.getMessage());
            } catch (IOException e) {
                printMessage("couldn't create table: " + e.getMessage());
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
        } catch (IOException e) {
            printMessage(e.getMessage());
            return false;
        }
        return true;
    }

    public TableMember getCurrentTable() {
        return currentTable;
    }

    public String serialize(Storeable storiable) throws ParseException {
        return provider.serialize(currentTable, storiable);
    }

    public Storeable deserialize(String string) throws ColumnFormatException, ParseException {
        return provider.deserialize(currentTable, string);
    }

    @Override
    public void printSuggestMessage() {
        if (currentTable != null) {
            outputStream.print(currentTable.getName());
        }
        outputStream.print("$");
    }
}
