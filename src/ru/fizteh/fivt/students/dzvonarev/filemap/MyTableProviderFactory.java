package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTableProviderFactory implements TableProviderFactory, AutoCloseable {

    private List<MyTableProvider> tableProvidersList;
    private boolean isTableProviderFactoryClosed;
    private Lock readLock;
    private Lock writeLock;

    public MyTableProviderFactory() {
        tableProvidersList = new ArrayList<>();
        isTableProviderFactoryClosed = false;
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();
    }

    @Override
    public MyTableProvider create(String dir) throws IOException, RuntimeException {
        if (isTableProviderFactoryClosed) {
            throw new IllegalStateException("table provider factory " + this.getClass().getSimpleName() + " is closed");
        }
        if (dir == null || dir.trim().isEmpty()) {
            throw new IllegalArgumentException("wrong type (invalid name of table provider)");
        }
        File providerFile = new File(dir);
        if (!providerFile.exists()) {
            if (!providerFile.mkdirs()) {
                throw new IOException("can't create provider in " + dir);
            }
        } else {
            if (!providerFile.isDirectory()) {
                throw new IllegalArgumentException("wrong type (table provider is not a directory)");
            }
        }
        MyTableProvider newTableProvider = new MyTableProvider(dir, this);
        tableProvidersList.add(newTableProvider);
        return newTableProvider;  // will read data in here
    }

    @Override
    public void close() {
        if (isTableProviderFactoryClosed) {
            return;
        }
        writeLock.lock();
        try {
            for (MyTableProvider tableProvider : tableProvidersList) {
                tableProvider.close();
            }
            tableProvidersList.clear();
            isTableProviderFactoryClosed = true;
        } finally {
            writeLock.unlock();
        }
    }

    public void removeClosedProvider(MyTableProvider provider) {
        tableProvidersList.remove(provider);
    }
}
