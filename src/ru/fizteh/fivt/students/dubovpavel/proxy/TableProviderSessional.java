package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.FileRepresentativeDataBase;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;
import ru.fizteh.fivt.students.dubovpavel.parallel.TableProviderStoreableParallel;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreableBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableProviderSessional<DB extends FileRepresentativeDataBase<Storeable>
        & Table & AutoCloseable & ClosedCheckable>
        extends TableProviderStoreableParallel<DB> implements AutoCloseable, ClosedCheckable {
    private boolean closed;
    private ReentrantReadWriteLock closingLock;

    @Override
    public String toString() {
        return ProxyUtils.generateRepr(this, storage.getPath());
    }

    public TableProviderSessional(Storage storage, TableStoreableBuilder builder) {
        super(storage, builder);
        closed = false;
        closingLock = new ReentrantReadWriteLock(true);
    }

    private void checkIfAlive() {
        if (closed) {
            throw new IllegalStateException("TableProvider is closed");
        }
    }

    public boolean closed() {
        try {
            closingLock.readLock().lock();
            return closed;
        } finally {
            closingLock.readLock().unlock();
        }
    }

    public void close() {
        try {
            closingLock.writeLock().lock();
            if (!closed) {
                for (Iterator<DB> i = storage.getDBIterator(); i.hasNext(); ) {
                    try {
                        i.next().close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                closed = true;
            }
        } finally {
            closingLock.writeLock().unlock();
        }
    }

    @Override
    public Table createTable(final String name, final List<Class<?>> columnTypes) throws IOException {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            return super.createTable(name, columnTypes);
        } finally {
            closingLock.readLock().unlock();
        }
    }

    @Override
    public DB getTable(final String name) {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            try {
                lock.readLock().lock();
                DB table = super.getTable(name);
                if (table != null && table.closed()) {
                    return storage.reOpenDataBase(table.getPath());
                } else {
                    return table;
                }
            } finally {
                lock.readLock().unlock();
            }
        } finally {
            closingLock.readLock().unlock();
        }
    }

    @Override
    public void removeTable(final String name) throws IOException {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            super.removeTable(name);
        } finally {
            closingLock.readLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            return super.deserialize(table, value);
        } finally {
            closingLock.readLock().unlock();
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            return super.serialize(table, value);
        } finally {
            closingLock.readLock().unlock();
        }
    }

    @Override
    public Storeable createFor(Table table) {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            return super.createFor(table);
        } finally {
            closingLock.readLock().unlock();
        }
    }

    @Override
    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            return super.createFor(table, values);
        } finally {
            closingLock.readLock().unlock();
        }
    }
}
