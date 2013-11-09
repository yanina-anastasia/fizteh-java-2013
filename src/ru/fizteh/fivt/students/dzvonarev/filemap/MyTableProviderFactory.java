package ru.fizteh.fivt.students.dzvonarev.filemap;


import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MyTableProviderFactory implements TableProviderFactory {

    @Override
    public MyTableProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("directory name is not valid");
        }
        if (!new File(dir).exists() ||
                !(new File(dir).exists() && new File(dir).isDirectory())) {
            throw new IllegalArgumentException("directory name is not valid");
        }
        MyTableProvider tableProvider = null;
        try {
            tableProvider = new MyTableProvider(dir);
        } catch (IOException e) {
            throw new IllegalArgumentException("can't read tables from " + dir);
        } finally {
            return tableProvider;
        }
    }
}
