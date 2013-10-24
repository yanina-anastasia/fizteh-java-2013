package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) throws IOException {
        return new MultiFileHashMapTableProvider(new File(dir));
    }
}
