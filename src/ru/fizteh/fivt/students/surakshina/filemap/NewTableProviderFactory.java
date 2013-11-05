package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;

import ru.fizteh.fivt.storage.strings.*;

public class NewTableProviderFactory implements TableProviderFactory {
    public NewTableProviderFactory() {
    }

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("No directory");
        }
        File directory = new File(dir);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }
        
        return new NewTableProvider(directory);
    }

}
