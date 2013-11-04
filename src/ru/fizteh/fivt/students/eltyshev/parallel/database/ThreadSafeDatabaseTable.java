package ru.fizteh.fivt.students.eltyshev.parallel.database;

import ru.fizteh.fivt.students.eltyshev.storable.database.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeDatabaseTable extends DatabaseTable {
    private final Lock transactionLock = new ReentrantLock(true);

    public ThreadSafeDatabaseTable(DatabaseTableProvider provider, String databaseDirectory, String tableName, List<Class<?>> columnTypes) {
        super(provider, databaseDirectory, tableName, columnTypes);
    }

    @Override
    public int commit() throws IOException {
        try {
            transactionLock.lock();
            return super.commit();
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public int rollback() {
        try {
            transactionLock.lock();
            return super.rollback();
        } finally {
            transactionLock.unlock();
        }
    }
}
