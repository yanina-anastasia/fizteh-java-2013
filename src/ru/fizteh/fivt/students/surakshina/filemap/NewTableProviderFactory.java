package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class NewTableProviderFactory implements TableProviderFactory {
    public NewTableProviderFactory() {
    }

    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("No directory");
        }
        File directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Can't create a directory");
            }
        } else {
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException("Not a directory");
            }
        }
        return new NewTableProvider(directory);
    }

}
