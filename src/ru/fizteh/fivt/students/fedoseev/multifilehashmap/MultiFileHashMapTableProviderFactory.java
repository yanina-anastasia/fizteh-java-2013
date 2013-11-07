package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MultiFileHashMapTableProviderFactory implements TableProviderFactory {
    @Override
    public MultiFileHashMapTableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("CREATE ERROR: incorrect directory name");
        }

        MultiFileHashMapTableProvider tb = new MultiFileHashMapTableProvider(dir);

        tb.createTable(dir);

        return tb;
    }
}
