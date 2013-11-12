package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.storage.structured.*;

import java.io.File;
import java.io.IOException;

public class DBTableProviderFactory implements TableProviderFactory {

    public TableProvider create(String rootDir) throws IOException {
        if (rootDir == null) {
            throw new IllegalArgumentException("Directory name can not be null");
        }
        TableProvider newTableProvider = null;
        File file = new File(rootDir);
        newTableProvider = new DBTableProvider(file);
        return newTableProvider;
    }
}
