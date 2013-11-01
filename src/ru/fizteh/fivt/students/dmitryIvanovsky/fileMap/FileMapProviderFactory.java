package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMapProviderFactory implements TableProviderFactory {

    public FileMapProvider create(String dir) {
        if (dir == null || dir.equals("")) {
            throw new IllegalArgumentException();
        }
        Path pathTables = Paths.get(".").resolve(dir);
        try {
            return new FileMapProvider(pathTables.toFile().getCanonicalPath());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}
