package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;

public class TableMember implements Table {

    protected DistributedTable table;
    protected DistributedTableProvider provider;
    protected HashMap<String, String> changes;
    protected ReadWriteLock tableLock;
    protected ReadWriteLock providerLock;
    protected String name;

    public TableMember(DistributedTable table, DistributedTableProvider provider, ReadWriteLock providerLock) {
        this.table = table;
        this.provider = provider;
        changes = new HashMap<>();
        tableLock = table.getLock();
        name = table.getName();
        this.providerLock = providerLock;
    }

    protected void checkExistence() {
        providerLock.readLock().lock();
        if (!provider.containsMember(name)) {
            throw new IllegalStateException("table not exists now");
        }
    }

    public int changesSize() {
        return changes.size();
    }

    @Override
    public String getName() {
        try {
            providerLock.readLock().lock();
            checkExistence();
            return name;
        } finally {
            providerLock.readLock().unlock();
        }
    }

    @Override
    public int rollback() {
        try {
            providerLock.readLock().lock();
            checkExistence();
            try {
                tableLock.writeLock().lock();
                merge();
                int canceled = table.rollback();
                changes.clear();
                return canceled;
            } finally {
                tableLock.writeLock().unlock();
            }

        } finally {
            providerLock.readLock().unlock();
        }
    }

    @Override
    public TableRecord get(String key) throws IllegalArgumentException {
        try {
            providerLock.readLock().lock();
            checkExistence();
            if (!table.isValidKey(key)) {
                throw new IllegalArgumentException("invalid key format");
            }
            String value;
            if (changes.containsKey(key)) {
                value = changes.get(key);
            } else {
                try {
                    tableLock.readLock().lock();
                    value = table.get(key);
                } finally {
                    tableLock.readLock().unlock();
                }
            }
            try {
                return provider.deserialize(this, value);
            } catch (ParseException e) {
                throw new IllegalStateException("error with deserializeing vale");
            }
        } finally {
            providerLock.readLock().unlock();
        }
    }

    @Override
    public TableRecord put(String key, Storeable value) throws ColumnFormatException {
        try {
            providerLock.readLock().lock();
            checkExistence();
            if (!table.isValidKey(key) || value == null) {
                throw new IllegalArgumentException("invalid key or value");
            }
            TableRecord old = get(key);
            String stringValue;
            stringValue = provider.serialize(this, value);
            changes.put(key, stringValue);
            return old;
        } finally {
            providerLock.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        try {
            providerLock.readLock().lock();
            checkExistence();
            if (!table.isValidKey(key)) {
                throw new IllegalArgumentException();
            }
            Storeable old = get(key);
            changes.put(key, null);
            return old;
        } finally {
            providerLock.readLock().unlock();
        }
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
        try {
            providerLock.readLock().lock();
            checkExistence();
            try {
                tableLock.writeLock().lock();
                merge();
                int recordNumber = table.size();
                table.rollback();
                return recordNumber;
            } finally {
                tableLock.writeLock().unlock();
            }
        } finally {
            providerLock.readLock().unlock();
        }
    }

    @Override
    public int commit() {
        try {
            providerLock.readLock().lock();
            checkExistence();
            try {
                tableLock.writeLock().lock();
                merge();
                int changed = table.commit();
                changes.clear();
                return changed;
            } finally {
                tableLock.writeLock().unlock();
            }
        } finally {
            providerLock.readLock().unlock();
        }
    }

    public int getColumnsCount() {
        try {
            providerLock.readLock().lock();
            checkExistence();
            return provider.getTableTypes(getName()).size();
        } finally {
            providerLock.readLock().unlock();
        }
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        try {
            providerLock.readLock().lock();
            checkExistence();
            return provider.getTableTypes(getName()).get(columnIndex);
        } finally {
            providerLock.readLock().unlock();
        }
    }
}

