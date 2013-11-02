package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DispatcherMultiFileHashMap;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;

public class TableProviderStorageExtendedFactory implements TableProviderFactory {
    public TableProvider create(String dir) {
        if(dir == null || dir.trim().equals("")) {
            throw new IllegalArgumentException("TableProviderFactory.create: dir is null");
        }
        DispatcherMultiFileHashMap dispatcher = new DispatcherMultiFileHashMap(false, false, dir, new WrappedMindfulDataBaseMultiFileHashMapBuilder());
        WrappedMindfulDataBaseMultiFileHashMapBuilder builder = new WrappedMindfulDataBaseMultiFileHashMapBuilder();
        builder.setDispatcher(dispatcher);
        Storage<WrappedMindfulDataBaseMultiFileHashMap> storage = new Storage<>(dir, dispatcher, builder);
        return new TableProviderStorageExtended(storage);
    }
}
