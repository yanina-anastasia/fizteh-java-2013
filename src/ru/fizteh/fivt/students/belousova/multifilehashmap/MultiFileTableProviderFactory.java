package ru.fizteh.fivt.students.belousova.multifilehashmap;

import java.io.File;
import java.io.IOException;

public class MultiFileTableProviderFactory implements ChangesCountingTableProviderFactory {
    @Override
    public MultiFileTableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("null directory");
        }
        if (dir.isEmpty()) {
            throw new IllegalArgumentException("empty directory");
        }
        MultiFileTableProvider tableProvider = null;
        try {
            File file = new File(dir);
            file.mkdir();
            tableProvider = new MultiFileTableProvider(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return tableProvider;
    }
}
