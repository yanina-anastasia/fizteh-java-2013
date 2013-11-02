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
        if(name == null || !IsKeyAllowable.check(name)) {
            throw new IllegalArgumentException();
        }
        return storage.getDataBase(name);
    }

    public Table createTable(String name) {
        if(name == null || !IsKeyAllowable.check(name)) {
            throw new IllegalArgumentException();
        }
        return storage.create(name);
    }

    public void removeTable(String name) {
        if(name == null || !IsKeyAllowable.check(name)) {
            throw new IllegalArgumentException();
        }
        storage.drop(name);
    }
}
