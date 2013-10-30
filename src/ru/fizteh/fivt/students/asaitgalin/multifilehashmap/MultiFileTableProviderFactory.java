package ru.fizteh.fivt.students.asaitgalin.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

public class MultiFileTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("factory: directory name is null");
        }
        return new MultiFileTableProvider(new File(dir));
    }
}
