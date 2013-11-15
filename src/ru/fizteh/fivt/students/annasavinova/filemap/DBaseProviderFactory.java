package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class DBaseProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) throws IOException {
        if (dir == null) {
            IllegalArgumentException e = new IllegalArgumentException("dir not selected");
            throw e;
        }
        if (!(new File(dir).exists())) {
            IllegalArgumentException e = new IllegalArgumentException("Directory not exists");
            throw e;
        }
        if (!(new File(dir).isDirectory())) {
            IllegalArgumentException e = new IllegalArgumentException("Not a directory");
            throw e;
        }
        DataBaseProvider dataBase = null;
        dataBase = new DataBaseProvider(dir);
        return dataBase;
    }
}
