package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMapProviderFactory implements TableProviderFactory {

    public FileMapProvider create(String dir) {
        if (dir == null || dir.equals("")) {
            throw new IllegalArgumentException();
        }
        File file = null;
        try {
            file = new File(dir);
        } catch (Exception e) {
            throw new IllegalArgumentException(dir + " directory doesn't open");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException(dir + " not exists");
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(dir + " isn't directory");
        }
        Path pathTables = Paths.get(".").resolve(dir);
        try {
            return new FileMapProvider(pathTables.toFile().getCanonicalPath());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}
