package ru.fizteh.fivt.students.eltyshev.storable.database;

import ru.fizteh.fivt.storage.structured.*;

import java.io.File;

public class DatabaseTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String directory) {
        File databaseDirectory = new File(directory);
        if (!databaseDirectory.exists()) {
            databaseDirectory.mkdir();
        }
        return new DatabaseTableProvider(databaseDirectory.getAbsolutePath());
    }
}
