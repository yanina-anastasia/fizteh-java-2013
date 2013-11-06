package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;

public class DatabaseTableProviderFactory implements TableProviderFactory {
    public DatabaseTableProvider create(String directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Error while getting property");
        }
        File databaseDirectory = new File(directory);
        if (!databaseDirectory.exists()) {
            if (!databaseDirectory.mkdir()) {
                throw new IllegalArgumentException("Error while getting property");
            }
        }
        if ((directory.isEmpty()) || (!databaseDirectory.isDirectory())) {
            throw new IllegalArgumentException("Error while getting property");
        }
        DatabaseTableProvider provider = new DatabaseTableProvider(databaseDirectory.getAbsolutePath());
        return provider;
    }
}
