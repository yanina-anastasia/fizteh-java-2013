package ru.fizteh.fivt.students.eltyshev.multifilemap;

import ru.fizteh.fivt.storage.strings.*;

import java.io.File;

public class DatabaseFactory implements TableProviderFactory {
    public TableProvider create(String directory) {
        if (directory == null || directory.equals(""))
        {
            throw new IllegalArgumentException("directory name cannot be empty");
        }
        File databaseDirectory = new File(directory);
        if (!databaseDirectory.exists()) {
            databaseDirectory.mkdir();
        }
        return new DatabaseTableProvider(databaseDirectory.getAbsolutePath());
    }
}
