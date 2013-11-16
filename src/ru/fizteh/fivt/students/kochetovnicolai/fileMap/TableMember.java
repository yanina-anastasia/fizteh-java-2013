package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.HashMap;

public class TableMember implements Table {

    protected DistributedTable table;
    protected DistributedTableProvider provider;
    protected HashMap<String, String> changes;

    public TableMember(DistributedTable table, DistributedTableProvider provider) {
        this.table = table;
        this.provider = provider;
        changes = new HashMap<>();
    }

    protected void checkExistence() {
        if (!provider.tables.containsValue(table)) {
            String name = table.getName();
            table = null;
            throw new RuntimeException(name + " not exists now");
        }
    }

    public int changesSize() {
        return changes.size();
    }

    @Override
    public String getName() {
        checkExistence();
        return table.getName();
    }

    @Override
    public int rollback() {
        checkExistence();
        merge();
        int canceled = table.rollback();
        changes.clear();
        return canceled;
    }

    @Override
    public TableRecord get(String key) throws IllegalArgumentException {
        checkExistence();
        if (!table.isValidKey(key)) {
            throw new IllegalArgumentException();
        }
        String value;
        if (changes.containsKey(key)) {
            value = changes.get(key);
        } else {
            value = table.get(key);
        }
        try {
            return provider.deserialize(this, value);
        } catch (ParseException e) {
            throw new IllegalStateException("error with deserializeing vale");
        }
    }

    @Override
    public TableRecord put(String key, Storeable value) throws ColumnFormatException {
        checkExistence();
        if (!table.isValidKey(key) || value == null) {
            throw new IllegalArgumentException("invalid key or value");
        }
        TableRecord old = get(key);
        String stringValue;
        stringValue = provider.serialize(this, value);
        changes.put(key, stringValue);
        return old;
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        checkExistence();
        if (!table.isValidKey(key)) {
            throw new IllegalArgumentException();
        }
        Storeable old = get(key);
        changes.put(key, null);
        return old;
    }

    protected void merge() {
        for (String key : changes.keySet()) {
            String value = changes.get(key);
            if (value != null) {
                table.put(key, changes.get(key));
            } else {
                table.remove(key);
            }
        }
    }

    @Override
    public int size() {
        checkExistence();
        merge();
        int recordNumber = table.size();
        table.rollback();
        return recordNumber;
    }

    @Override
    public int commit() {
        checkExistence();
        merge();
        int changed = table.commit();
        changes.clear();
        return changed;
    }

    public int getColumnsCount() {
        return provider.getTableTypes(getName()).size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return provider.getTableTypes(getName()).get(columnIndex);
    }
}

