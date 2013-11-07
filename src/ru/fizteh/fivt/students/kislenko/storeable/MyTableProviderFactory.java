package ru.fizteh.fivt.students.kislenko.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MyTableProviderFactory implements TableProviderFactory {
    @Override
    public MyTableProvider create(String path) throws IOException {
        if (path == null || path.trim().equals("")) {
            throw new IllegalArgumentException("Incorrect database name.");
        }
        File file = new File(path.trim());
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Database must be a directory.");
        }
        return new MyTableProvider();
    }
}
