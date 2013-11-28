package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DatabaseTableProviderFactory implements TableProviderFactory, AutoCloseable {
    boolean isClosed = false;
    public Set<DatabaseTableProvider> providers = new HashSet<>();

    public DatabaseTableProvider create(String directory) throws IOException {
        if (isClosed) {
            throw new IllegalStateException("It is closed");
        }
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
        providers.add(provider);
        return provider;
    }

    @Override
    public void close() throws Exception {
        if (!isClosed) {
            for (DatabaseTableProvider provider: providers) {
                provider.close();
            }
            isClosed = true;
        }
    }
}
