package ru.fizteh.fivt.students.baranov.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MyTableProviderFactory implements TableProviderFactory {
    public MyTableProvider create(String directory) throws IOException {
        if (directory == null || directory.isEmpty()) {
            throw new IllegalArgumentException("Error while getting property");
        }
        File databaseDirectory = new File(directory);
        if (!databaseDirectory.exists()) {
            if (!databaseDirectory.mkdir()) {
                throw new IOException("Error while getting property");
            }
        }
        if ((directory.isEmpty()) || (!databaseDirectory.isDirectory())) {
            throw new IllegalArgumentException("Error while getting property");
        }
        MyTableProvider provider = new MyTableProvider(databaseDirectory.getAbsolutePath());
        return provider;
    }
}
