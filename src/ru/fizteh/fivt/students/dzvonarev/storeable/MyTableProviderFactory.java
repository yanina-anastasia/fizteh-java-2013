package ru.fizteh.fivt.students.dzvonarev.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MyTableProviderFactory implements TableProviderFactory {

    @Override
    public MyTableProvider create(String dir) throws IOException, RuntimeException {
        if (dir == null) {
            throw new IllegalArgumentException("directory name is not valid");
        }
        if (!new File(dir).exists() ||
                !(new File(dir).exists() && new File(dir).isDirectory())) {
            throw new IllegalArgumentException("directory name is not valid");
        }
        return new MyTableProvider(dir);  // will read data in here
    }
}
