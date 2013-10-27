package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.HashMap;

public class TableMember implements Table {
    protected int recordNumber;
    protected int oldRecordNumber;

    protected DistributedTable table;
    protected DistributedTableProvider provider;
    HashMap<String, String> changes;

    TableMember(DistributedTable table, DistributedTableProvider provider) {
        this.table = table;
        this.provider = provider;
        oldRecordNumber = recordNumber = table.getRecordNumber();
        changes = new HashMap<>();
    }

    protected void checkExistence() {
        if (!provider.tables.containsValue(table)) {
            String name = table.getName();
            table = null;
            throw new RuntimeException(name + " not exists now");
        }
    }

    @Override
    public String getName() {
        checkExistence();
        return table.getName();
    }

    @Override
    public int rollback() {
        checkExistence();
        int canceled = recordNumber - oldRecordNumber;
        recordNumber = oldRecordNumber;
        changes.clear();
        return canceled;
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        checkExistence();
        if (key == null) {
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
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        if (get(key) == null) {
            recordNumber++;
        }
        String old = table.get(key);
        changes.put(key, value);
        return old;
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        checkExistence();
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (get(key) != null) {
            recordNumber--;
        }
        return changes.put(key, null);
    }

    @Override
    public int size() {
        checkExistence();
        return recordNumber;
    }

    @Override
    public int commit() {
        checkExistence();
        for (String key : changes.keySet()) {
            table.put(key, changes.get(key));
        }
        return table.commit();
    }
}

