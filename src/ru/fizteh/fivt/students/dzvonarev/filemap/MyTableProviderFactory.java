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
        File providerFile = new File(dir);
        if (!providerFile.exists()) {
            if (!providerFile.mkdir()) {
                throw new IllegalArgumentException("can't create provider in " + dir);
            }
        } else {
            if (!providerFile.isDirectory()) {
                throw new IllegalArgumentException("directory name is not valid");
            }
        }
        MyTableProvider tableProvider;
        try {
            tableProvider = new MyTableProvider(dir);
        } catch (IOException e) {
            throw new IllegalArgumentException("can't read tables from " + dir);
        }
        return tableProvider;
    }
}
