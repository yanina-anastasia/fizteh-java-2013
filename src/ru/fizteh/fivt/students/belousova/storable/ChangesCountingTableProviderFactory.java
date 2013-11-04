package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public interface ChangesCountingTableProviderFactory extends TableProviderFactory {
    ChangesCountingTableProvider create(String dir);
}
