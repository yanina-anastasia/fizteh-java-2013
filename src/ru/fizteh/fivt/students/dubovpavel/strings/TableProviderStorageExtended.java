package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;

public class TableProviderStorageExtended implements TableProvider{
    Storage<WrappedMindfulDataBaseMultiFileHashMap> storage;
    public TableProviderStorageExtended(Storage storage) {
        this.storage = storage;
    }

    public Table getTable(String name) {
        if(name == null) {
            throw new IllegalArgumentException("TableProvide.getTable: name is null");
        }
        return storage.getDataBase(name);
    }

    public Table createTable(String name) {
        if(name == null) {
            throw new IllegalArgumentException("TableProvider.createTable: name is null");
        }
        return storage.create(name);
    }

    public void removeTable(String name) {
        if(name == null) {
            throw new IllegalArgumentException("TableProvider.removeTable: name is null");
        }
        storage.drop(name);
    }
}
