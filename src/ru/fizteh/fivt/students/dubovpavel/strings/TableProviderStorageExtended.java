package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;

public class TableProviderStorageExtended implements TableProvider{
    Storage<WrappedMindfulDataBaseMultiFileHashMap> storage;

    private boolean isNameValid(String name) {
        return !name.contains("\\") && !name.contains("/");
    }

    public TableProviderStorageExtended(Storage storage) {
        this.storage = storage;
    }

    public Table getTable(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if(!isNameValid(name)) {
            throw new RuntimeException();
        }
        return storage.getDataBase(name);
    }

    public Table createTable(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if(!isNameValid(name)) {
            throw new RuntimeException();
        }
        return storage.create(name);
    }

    public void removeTable(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if(storage.drop(name) == null) {
            throw new IllegalStateException();
        }
        storage.drop(name);
    }
}
