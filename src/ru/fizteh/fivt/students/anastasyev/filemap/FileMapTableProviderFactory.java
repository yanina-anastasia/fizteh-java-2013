package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class FileMapTableProviderFactory implements TableProviderFactory {
    @Override
    public FileMapTableProvider create(String dir) throws IOException {
        if (dir == null || dir.trim().trim().isEmpty()) {
            throw new IllegalArgumentException("Directory path can't be null");
        }
        File fileDir = new File(dir.trim());
        if (fileDir.isFile()) {
            throw new IllegalArgumentException("Wrong directory path");
        }
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                throw new IOException("Can't create directory");
            }
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
