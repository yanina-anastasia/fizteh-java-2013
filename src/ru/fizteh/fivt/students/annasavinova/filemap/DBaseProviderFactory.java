package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.File;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class DBaseProviderFactory implements TableProviderFactory {
    private static String root = "";

    public String getRoot() {
        return root;
    }

    public DBaseProviderFactory() throws RuntimeException {
        if (System.getProperty("fizteh.db.dir") == null) {
            throw new RuntimeException("root dir not selected");
        }
        File r = new File(System.getProperty("fizteh.db.dir"));
        if (!r.exists()) {
            if (!r.mkdir()) {
                throw new RuntimeException("cannot create root dir");
            }
        }
        if (System.getProperty("fizteh.db.dir").endsWith(File.separator)) {
            root = System.getProperty("fizteh.db.dir");
        } else {
            root = System.getProperty("fizteh.db.dir") + File.separatorChar;
        }
    }

    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
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
        DataBaseProvider dataBase = new DataBaseProvider(dir);
        return dataBase;
    }
}
