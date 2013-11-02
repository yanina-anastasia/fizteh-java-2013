package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

public class FileMapProviderFactory implements TableProviderFactory {

    public FileMapProvider create(String location) {
        if (location == null) {
            throw new IllegalArgumentException("Null location");
        }
        FileMapProvider newProvider = new FileMapProvider(new File(location));
        if (!newProvider.isValidLocation()) {
            throw new IllegalArgumentException("Database location is invalid");
        }
        if (!newProvider.isValidContent()) {
            throw new IllegalArgumentException("Database folder contains files");
        }
        return newProvider;
    }
}
