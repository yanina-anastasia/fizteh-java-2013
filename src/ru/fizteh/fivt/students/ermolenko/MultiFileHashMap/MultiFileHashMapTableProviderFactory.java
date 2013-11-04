package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapTableProviderFactory implements TableProviderFactory {

    @Override
    public MultiFileHashMapTableProvider create(String dir) {

        try {
            return new MultiFileHashMapTableProvider(new File(dir));
        } catch (IOException e) {
            System.err.println(e);
        }
        return null;
    }
}
