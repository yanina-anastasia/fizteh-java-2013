package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.parallel.TableStoreableParallel;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableSessional extends TableStoreableParallel implements AutoCloseable, ClosedCheckable {
    private boolean closed;
    private ReentrantReadWriteLock closingLock;

    public TableSessional(File path, Dispatcher dispatcher, ArrayList<Class<?>> types) {
        super(path, dispatcher, types);
        closed = false;
        closingLock = new ReentrantReadWriteLock(true);
    }

    private void checkIfAlive() {
        if (closed) {
            throw new IllegalStateException("Table was closed");
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

    public void close() throws Exception {
        try {
            closingLock.writeLock().lock();
            if (!closed) {
                super.rollback();
                closed = true;
            }
        } finally {
            closingLock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        return ProxyUtils.generateRepr(this, root.getAbsolutePath());
    }

    // getName is not overridden on purpose

    @Override
    public int size() {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            return super.size();
        } finally {
            closingLock.readLock().unlock();
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            return super.put(key, value);
        } finally {
            closingLock.readLock().unlock();
        }
    }

    @Override
    public Storeable get(String key) {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            return super.get(key);
        } finally {
            closingLock.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            return super.remove(key);
        } finally {
            closingLock.readLock().unlock();
        }
    }

    @Override
    public int rollback() {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            return super.rollback();
        } finally {
            closingLock.readLock().unlock();
        }
    }

    @Override
    public int commit() {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            return super.commit();
        } finally {
            closingLock.readLock().unlock();
        }
    }
}
