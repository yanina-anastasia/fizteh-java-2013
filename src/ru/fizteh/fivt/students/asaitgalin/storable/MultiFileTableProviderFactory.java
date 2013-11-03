package ru.fizteh.fivt.students.asaitgalin.storable;

import ru.fizteh.fivt.students.asaitgalin.storable.extensions.ExtendedTableProvider;
import ru.fizteh.fivt.students.asaitgalin.storable.extensions.ExtendedTableProviderFactory;

import java.io.File;

public class MultiFileTableProviderFactory implements ExtendedTableProviderFactory {
    @Override
    public ExtendedTableProvider create(String dir) {
        if (dir == null || dir.trim().isEmpty()) {
            throw new IllegalArgumentException("factory: directory name is invalid");
        }
        return new MultiFileTableProvider(new File(dir));
    }
}
