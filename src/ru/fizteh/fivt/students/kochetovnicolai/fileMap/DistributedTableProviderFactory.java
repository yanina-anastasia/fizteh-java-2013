package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DistributedTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private HashMap<String, DistributedTableProvider> providers;
    private final Lock lock = new ReentrantLock();

    private void checkState() throws IllegalStateException {
        if (providers == null) {
            throw new IllegalStateException("factory already closed");
        }
    }

    @Override
    public void close() throws IOException {
        try {
            checkState();
        } catch (IllegalStateException e) {
            return;
        }
        lock.lock();
        try {
            for (DistributedTableProvider provider : providers.values()) {
                provider.close();
            }
            providers = null;
        } finally {
            lock.unlock();
        }
    }

    public DistributedTableProviderFactory() {
        providers = new HashMap<>();
    }

    @Override
    public DistributedTableProvider create(String dir) throws IOException, IllegalArgumentException {
        checkState();
        if (dir == null || dir.equals("")) {
            throw new IllegalArgumentException("directory couldn't be null");
        }
        File path = new File(dir);
        try {
            path = path.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid directory", e);
        }
        String directory = path.getAbsolutePath();
        lock.lock();
        try {
            if (!providers.containsKey(directory)) {
                providers.put(directory, new DistributedTableProvider(path.toPath()));
            }
            return providers.get(directory);
        } finally {
            lock.unlock();
        }
    }
}
