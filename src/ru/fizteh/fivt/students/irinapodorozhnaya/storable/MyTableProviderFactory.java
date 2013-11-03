package ru.fizteh.fivt.students.irinapodorozhnaya.storable;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.irinapodorozhnaya.storable.extend.ExtendProvider;

public class MyTableProviderFactory implements TableProviderFactory {
    
    @Override
    public ExtendProvider create(String dataBaseDir) throws IOException {
        if (dataBaseDir == null) {
           throw new IllegalArgumentException("dir not defined");
        }
        File directory = new File(dataBaseDir);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(dataBaseDir + " is not a directory name");
        }
        return new MyTableProvider(directory);
    }

}
