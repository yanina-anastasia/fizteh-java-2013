package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMapProviderFactory implements TableProviderFactory {

    public TableProvider create(String dir) throws IOException {
        if (dir == null || dir.equals("")) {
            throw new IllegalArgumentException();
        }
        File file = null;
        try {
            file = new File(dir);
        } catch (Exception e) {
            throw new IOException(dir + " directory doesn't open");
        }
        try {
            if (!file.isDirectory() && !file.mkdir()) {
                throw new IOException(dir + " not exists");
            }
        } catch (Exception e) {
            throw new IOException(dir + " not exists");
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(dir + " isn't directory");
        }
        Path pathTables = Paths.get(".").resolve(dir);
        try {
            TableProvider table = new FileMapProvider(pathTables.toFile().getCanonicalPath());
            return table;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}
