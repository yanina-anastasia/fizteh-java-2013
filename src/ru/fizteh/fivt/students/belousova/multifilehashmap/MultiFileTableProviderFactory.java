package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;
import java.io.IOException;

public class MultiFileTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        TableProvider tableProvider = null;
        try {
            File file = new File(dir);
            tableProvider = new MultiFileTableProvider(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return tableProvider;
    }
}
