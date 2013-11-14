package ru.fizteh.fivt.students.dzvonarev.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MyTableProviderFactory implements TableProviderFactory {

    @Override
    public MyTableProvider create(String dir) throws IOException, RuntimeException {
        if (dir == null || dir.trim().isEmpty()) {
            throw new IllegalArgumentException("directory name is not valid");
        }
        File providerFile = new File(dir);
        if (!providerFile.exists()) {
            if (!providerFile.mkdir()) {
                throw new IOException("can't create provider in " + dir);
            }
        } else {
            if (!providerFile.isDirectory()) {
                throw new IllegalArgumentException("directory name is not valid");
            }
        }
        return new MyTableProvider(dir);  // will read data in here
    }
}
