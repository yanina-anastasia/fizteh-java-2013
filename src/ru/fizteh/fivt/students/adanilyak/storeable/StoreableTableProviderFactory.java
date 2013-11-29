package ru.fizteh.fivt.students.adanilyak.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 16:50
 */
public class StoreableTableProviderFactory implements TableProviderFactory {
    private final Lock lock = new ReentrantLock(true);

    @Override
    public TableProvider create(String directoryWithTables) throws IOException {
        try {
            lock.lock();
            if (directoryWithTables == null || directoryWithTables.trim().isEmpty()) {
                throw new IllegalArgumentException("Directory not set or set incorrectly");
            }

            File file = new File(directoryWithTables);
            return new StoreableTableProvider(file);
        } finally {
            lock.unlock();
        }
    }
}
