package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class FileMapProviderFactory implements TableProviderFactory {

    public FileMapProvider create(String location) throws IOException {
        if (location == null) {
            throw new IllegalArgumentException("Null location");
        }
        if (location.equals("")) {
            throw new IllegalArgumentException("Empty location");
        }
        File path = new File(location);
        FileMapProvider newProvider = new FileMapProvider(path);
        if (path.exists() && !path.isDirectory()) {
            throw new IllegalArgumentException("File is located at specified location");
        }
        if (!newProvider.isValidLocation()) {
            throw new IOException("Database location is invalid");
        }
        if (!newProvider.isValidContent()) {
            throw new RuntimeException("Database folder contains files");
        }
        return newProvider;
    }
}
