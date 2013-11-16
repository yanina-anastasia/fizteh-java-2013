package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.storage.structured.*;

import java.io.File;
import java.io.IOException;

public class DBTableProviderFactory implements TableProviderFactory {
    private static final String TABLE_NAME_FORMAT = "[A-Za-zА-Яа-я0-9@.]+";
    public TableProvider create(String rootDir) throws IOException {
        if (rootDir == null || rootDir.trim().isEmpty() || !rootDir.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("wrong directory name");
        }
        TableProvider newTableProvider = null;
        File file = new File(rootDir);
        newTableProvider = new DBTableProvider(file);
        return newTableProvider;
    }
}
