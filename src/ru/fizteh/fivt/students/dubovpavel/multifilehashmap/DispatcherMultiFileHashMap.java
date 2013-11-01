package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseAccessible;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;

public class DispatcherMultiFileHashMap extends Dispatcher implements DataBaseAccessible<String, String>, StorageAccessible {
    protected Storage storage;
    public DispatcherMultiFileHashMap(boolean forwarding, String stPath, DataBaseBuilder builder) {
        super(forwarding);
        try {
            storage = new Storage(getInitProperty(stPath), this, builder);
        } catch (DispatcherException e) {
            storage = null;
        }
    }

    public DataBaseHandler<String, String> getDataBase() {
        return storage.getCurrent();
    }

    public Storage getStorage() {
        return storage;
    }
}
