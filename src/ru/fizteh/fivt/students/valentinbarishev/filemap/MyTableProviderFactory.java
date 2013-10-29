package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

public class MyTableProviderFactory implements TableProviderFactory{

    public MyTableProviderFactory() {
    }

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Dir cannot be null");
        }

        File tableDirFile = new File(dir);

        if (!tableDirFile.exists()) {
            if (!tableDirFile.mkdir()) {
                throw new IllegalArgumentException("Cannot create directory!");
            }
        }

        if (!tableDirFile.isDirectory()) {
            throw new IllegalArgumentException("Wrong dir " + dir);
        }

        return new DataBaseTable(dir);
    }
}
