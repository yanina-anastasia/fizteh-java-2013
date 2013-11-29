package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;

public class StringTableProviderStorageExtended extends TableProviderStorageExtended<StringWrappedMindfulDataBaseMultiFileHashMap> implements TableProvider {
    public StringTableProviderStorageExtended(Storage storage) {
        super(storage);
    }
}
