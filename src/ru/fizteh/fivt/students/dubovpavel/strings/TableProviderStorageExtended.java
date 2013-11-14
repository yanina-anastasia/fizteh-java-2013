package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.FileRepresentativeDataBase;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;

public class TableProviderStorageExtended<DB extends FileRepresentativeDataBase> {
    protected Storage<DB> storage;

    private boolean isNameValid(String name) {
        return !name.contains("\\") && !name.contains("/");
    }

    public TableProviderStorageExtended(Storage storage) {
        this.storage = storage;
    }

    public DB getTable(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if(!isNameValid(name)) {
            throw new RuntimeException();
        }
        return storage.getDataBase(name);
    }

    public DB createTable(String name) {
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
