package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class DBaseProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) throws IOException {
        if (dir == null || dir.trim().isEmpty()) {
            throw new IllegalArgumentException("dir not selected");
        }
        File root = new File(dir);
        if (!root.exists()) {
            if (!root.mkdirs()) {
                throw new IOException("Directory cannot be created");
            }
        }
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }
        DataBaseProvider dataBase = null;
        dataBase = new DataBaseProvider(dir);
        return dataBase;
    }
}
