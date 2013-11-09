package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.irinapodorozhnaya.storeable.extend.ExtendProvider;

public class MyTableProviderFactory implements TableProviderFactory {

    public static final String LEGAL_NAME = "[^:*?\"<>|]+";

    @Override
    public ExtendProvider create(String dataBaseDir) throws IOException {
        if (dataBaseDir == null || dataBaseDir.trim().isEmpty() || !dataBaseDir.matches(LEGAL_NAME)) {
           throw new IllegalArgumentException("dir not defined or has illegal name");
        }
        
        File directory = new File(dataBaseDir);
        
        if (!directory.exists()) {
            throw new IOException(dataBaseDir + " is not a directory name");
        } else if (!directory.isDirectory()) {
            throw new IllegalArgumentException(dataBaseDir + " not a directory");    
        }
        
        return new MyTableProvider(directory);
    }

}
