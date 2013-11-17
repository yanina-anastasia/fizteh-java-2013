package ru.fizteh.fivt.students.inaumov.storeable.base;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.inaumov.storeable.base.DatabaseTableProvider;

import java.io.File;
import java.io.IOException;

public class DatabaseTableProviderFactory implements TableProviderFactory {
    @Override
    public synchronized TableProvider create(String directory) throws IOException {
        if (directory == null || directory.trim().isEmpty()) {
            throw new IllegalArgumentException("error: directory is null (or empty)");
        }

        File databaseDirectory = new File(directory);
        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException("error: database is placed in a file");
        }

        if (!databaseDirectory.exists()) {
            if (!databaseDirectory.mkdir()) {
                throw new IOException("error: provider is unavailable");
            }
        }

        return new DatabaseTableProvider(databaseDirectory.getAbsolutePath());
    }
}
