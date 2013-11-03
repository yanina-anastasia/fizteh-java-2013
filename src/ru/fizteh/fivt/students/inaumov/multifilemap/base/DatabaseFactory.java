package ru.fizteh.fivt.students.inaumov.multifilemap.base;

import ru.fizteh.fivt.storage.strings.*;

import java.io.File;

public class DatabaseFactory implements TableProviderFactory {
    public TableProvider create(String dir) {
        File databaseDir = new File(dir);
        if (!databaseDir.exists()) {
            databaseDir.mkdir();
        }

        return new DatabaseTableProvider(databaseDir.getAbsolutePath());
    }
}
