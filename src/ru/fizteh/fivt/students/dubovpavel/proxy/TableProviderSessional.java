package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.FileRepresentativeDataBase;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;
import ru.fizteh.fivt.students.dubovpavel.parallel.TableProviderStoreableParallel;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreableBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableProviderSessional<DB extends FileRepresentativeDataBase<Storeable> & Table & AutoCloseable>
        extends TableProviderStoreableParallel<DB> implements AutoCloseable {
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
            return super.getTable(name);
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
}
