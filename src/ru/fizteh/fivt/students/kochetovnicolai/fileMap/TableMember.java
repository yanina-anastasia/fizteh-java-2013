package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.strings.Table;

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
    public String get(String key) throws IllegalArgumentException {
        checkExistence();
        if (!table.isValidKey(key)) {
            throw new IllegalArgumentException();
        }
        if (changes.containsKey(key)) {
            return changes.get(key);
        }
        return table.get(key);
    }

    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        checkExistence();
        if (!table.isValidKey(key) || !table.isValidValue(value)) {
            throw new IllegalArgumentException("invalid key or value");
        }
        String old = get(key);
        changes.put(key, value);
        return old;
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        checkExistence();
        if (!table.isValidKey(key)) {
            throw new IllegalArgumentException();
        }
        String old = get(key);
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
        table.commit();
        int changed = changes.size();
        changes.clear();
        return changed;
    }
}

