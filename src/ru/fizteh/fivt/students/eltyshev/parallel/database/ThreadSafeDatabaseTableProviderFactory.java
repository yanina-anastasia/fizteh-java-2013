package ru.fizteh.fivt.students.eltyshev.parallel.database;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.eltyshev.storable.database.*;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeDatabaseTableProviderFactory extends DatabaseTableProviderFactory {
    private final Lock lock = new ReentrantLock(true);

    @Override
    public TableProvider create(String directory) throws IOException {
        try {
            lock.lock();
            return super.create(directory);
        } finally {
            lock.unlock();
        }
    }
}
