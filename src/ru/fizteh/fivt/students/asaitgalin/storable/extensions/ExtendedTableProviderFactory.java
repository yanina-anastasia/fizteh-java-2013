package ru.fizteh.fivt.students.asaitgalin.storable.extensions;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public interface ExtendedTableProviderFactory extends TableProviderFactory {
    @Override
    ExtendedTableProvider create(String dir);
}
