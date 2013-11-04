package ru.fizteh.fivt.students.eltyshev.parallel.database;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.eltyshev.storable.database.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeDatabaseTableProvider extends DatabaseTableProvider {
    private final Lock lock = new ReentrantLock(true);

    public ThreadSafeDatabaseTableProvider(String databaseDirectoryPath) {
        super(databaseDirectoryPath);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        try {
            lock.lock();
            return super.createTable(name, columnTypes);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Table getTable(String name) {
        try {
            lock.lock();
            return super.getTable(name);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        try {
            lock.lock();
            super.removeTable(name);
        } finally {
            lock.unlock();
        }
    }
}
