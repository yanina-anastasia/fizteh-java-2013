package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.storage.structured.*;

import java.io.File;

public class DBTableProviderFactory implements TableProviderFactory {

    public TableProvider create(String rootDir) throws IllegalArgumentException {
        if (rootDir == null) {
            throw new IllegalArgumentException("Directory name can not be null");
        }
        TableProvider newTableProvider = null;
        try {
            File file = new File(rootDir);
            newTableProvider = new DBTableProvider(file);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return newTableProvider;
    }
}
