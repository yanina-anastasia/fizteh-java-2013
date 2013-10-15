package ru.fizteh.fivt.students.eltyshev.multifilemap;

import ru.fizteh.fivt.storage.strings.*;

import java.io.File;

public class DatabaseFactory implements TableProviderFactory {
    public TableProvider create(String directory) {
        File databaseDirectory = new File(directory);
        if (!databaseDirectory.exists()) {
            databaseDirectory.mkdir();
        }
        return new Database(databaseDirectory.getAbsolutePath());
    }
}
