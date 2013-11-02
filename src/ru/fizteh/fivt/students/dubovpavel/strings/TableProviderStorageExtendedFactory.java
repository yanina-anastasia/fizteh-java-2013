package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DispatcherMultiFileHashMap;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;

public class TableProviderStorageExtendedFactory implements TableProviderFactory {
    public TableProvider create(String dir) {
        if(dir == null) {// || !IsKeyAllowable.check(dir)) {
            throw new IllegalArgumentException();
        }
        DispatcherMultiFileHashMap dispatcher = new DispatcherMultiFileHashMap(false, false, dir, new WrappedMindfulDataBaseMultiFileHashMapBuilder());
        WrappedMindfulDataBaseMultiFileHashMapBuilder builder = new WrappedMindfulDataBaseMultiFileHashMapBuilder();
        builder.setDispatcher(dispatcher);
        Storage<WrappedMindfulDataBaseMultiFileHashMap> storage = new Storage<>(dir, dispatcher, builder);
        return new TableProviderStorageExtended(storage);
    }
}
