package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseTableProviderFactory implements TableProviderFactory {
    private final Lock lock = new ReentrantLock(true);

    public DatabaseTableProvider create(String directory) throws IOException {
        try {
            lock.lock();
            if (directory == null || directory.isEmpty()) {
                throw new IllegalArgumentException("Error while getting property");
            }
            File databaseDirectory = new File(directory);
            if (!databaseDirectory.exists()) {
                if (!databaseDirectory.mkdir()) {
                    throw new IOException("Error while getting property");
                }
            }
            if ((directory.isEmpty()) || (!databaseDirectory.isDirectory())) {
                throw new IllegalArgumentException("Error while getting property");
            }
            DatabaseTableProvider provider = new DatabaseTableProvider(databaseDirectory.getAbsolutePath());
            return provider;
        } finally {
            lock.unlock();
        }
    }
}
