package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class FileMapTableProviderFactory implements TableProviderFactory {

    public FileMapTableProviderFactory() {
    }

    @Override
    public FileMapTableProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("Directory path can't be null");
        }
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            if (!fileDir.mkdir()) {
                throw new IllegalArgumentException("Can't create directory");
            }
        }
        if (!fileDir.isDirectory()) {
            throw new IllegalArgumentException("Wrong directory path");
        }
        FileMapTableProvider fileMapTableProvider = null;
        try {
            fileMapTableProvider = new FileMapTableProvider(fileDir.toString());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return fileMapTableProvider;
    }
}
