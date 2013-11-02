package ru.fizteh.fivt.students.eltyshev.storable.database;

import ru.fizteh.fivt.storage.structured.*;

import java.io.File;

public class DatabaseTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String directory) {
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }
        File databaseDirectory = new File(directory);
        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException("cannot create database in file. Provide a directory, please");
        }
        if (!databaseDirectory.exists()) {
            databaseDirectory.mkdir();
        }
        return new DatabaseTableProvider(databaseDirectory.getAbsolutePath());
    }
}
