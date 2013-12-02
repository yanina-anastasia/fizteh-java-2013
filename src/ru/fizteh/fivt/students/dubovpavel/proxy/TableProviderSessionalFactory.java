package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageBuilder;
import ru.fizteh.fivt.students.dubovpavel.parallel.TableProviderStoreableParallelFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableProviderSessionalFactory extends TableProviderStoreableParallelFactory
        implements AutoCloseable {
    private HashSet<TableProviderSessional> created = new HashSet<>();
    private ReentrantReadWriteLock closingLock = new ReentrantReadWriteLock(true);
    private boolean closed = false;

    private void checkIfAlive() {
        if (closed) {
            throw new IllegalStateException("TableProviderFactory is closed");
        }
    }

    public TableProvider create(String path) throws IOException {
        try {
            closingLock.readLock().lock();
            checkIfAlive();
            checkPath(path);
            StorageBuilder storageBuilder = new StorageBuilder();
            storageBuilder.setPath(false, path);
            TableSessionalBuilder dataBaseBuilder = new TableSessionalBuilder();
            storageBuilder.setDataBaseBuilder(dataBaseBuilder);
            TableProviderSessional provider =
                    new TableProviderSessional<TableSessional>(storageBuilder.construct(), dataBaseBuilder);
            created.add(provider);
            return provider;
        } finally {
            closingLock.readLock().unlock();
        }
    }

    public void close() {
        try {
            closingLock.writeLock().lock();
            if (!closed) {
                for (TableProviderSessional provider: created) {
                    provider.close();
                }
                closed = true;
            }
        } finally {
            closingLock.writeLock().unlock();
        }
    }
}
