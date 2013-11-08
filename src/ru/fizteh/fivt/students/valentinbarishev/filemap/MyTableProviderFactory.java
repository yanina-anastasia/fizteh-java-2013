package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MyTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) throws IOException {
        if (dir == null) {
            throw new IllegalArgumentException("Dir cannot be null");
        }

        File tableDirFile = new File(dir);

        if (!tableDirFile.exists()) {
            if (!tableDirFile.mkdirs()) {
                throw new IOException("Cannot create directory! " + tableDirFile.getCanonicalPath());
            }
        }

        if (!tableDirFile.isDirectory()) {
            throw new IllegalArgumentException("Wrong dir " + dir);
        }

        return new DataBaseTable(dir);
    }
}
