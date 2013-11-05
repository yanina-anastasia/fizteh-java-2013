package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class VorotilovTableProviderFactory implements TableProviderFactory {

    public VorotilovTableProviderFactory() {}

    //нужна проверка на допустимость самого пути
    @Override
    public VorotilovTableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Dir with tables is null");
        }
        if (dir.equals("")) {
            throw new IllegalArgumentException("Dir is empty string");
        }
        File rootDir;
        try {
            rootDir = (new File(dir)).getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't get path to root dir");
        }
        if (rootDir.exists()) {
            if (!rootDir.isDirectory()) {
                throw new IllegalArgumentException("Proposed object is not directory");
            }
        } else {
            if (!rootDir.mkdirs()) {
                throw new IllegalArgumentException("Can't create root directory");
            }
        }
        return new VorotilovTableProvider(rootDir);
    }

}
