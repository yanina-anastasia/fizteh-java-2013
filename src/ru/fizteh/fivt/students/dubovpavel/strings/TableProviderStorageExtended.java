package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.Storage;

import java.io.File;
import java.io.IOException;

public class TableProviderStorageExtended implements TableProvider{
    Storage<WrappedMindfulDataBaseMultiFileHashMap> storage;

    private boolean isNameValid(String name) {
        File litmus = new File(name);
        try {
            if(!litmus.getCanonicalFile().getName().equals(name)) {
                return false;
            }
        } catch(IOException e) {
            return false;
        }
        return true;
    }

    public TableProviderStorageExtended(Storage storage) {
        this.storage = storage;
    }

    public Table getTable(String name) {
        if(name == null || name.equals("")) {
            throw new IllegalArgumentException();
        }
        if(!isNameValid(name)) {
            throw new RuntimeException();
        }
        return storage.getDataBase(name);
    }

    public Table createTable(String name) {
        if(name == null || name.equals("")) {
            throw new IllegalArgumentException();
        }
        if(!isNameValid(name)) {
            throw new RuntimeException();
        }
        return storage.create(name);
    }

    public void removeTable(String name) {
        if(name == null || name.equals("")) {
            throw new IllegalArgumentException();
        }
        if(storage.drop(name) == null) {
            throw new IllegalStateException();
        }
        storage.drop(name);
    }
}
