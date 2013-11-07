package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseAccessible;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;

public class DispatcherMultiFileHashMap extends Dispatcher implements DataBaseAccessible<String, String>, StorageAccessible {
    protected Storage storage;
    public DispatcherMultiFileHashMap(boolean forwarding, boolean pathIsProperty, String path, DataBaseBuilder builder) {
        super(forwarding);
        if(pathIsProperty) {
            try {
                storage = new Storage(getInitProperty(path), this, builder);
            } catch (DispatcherException e) {
                System.exit(-1);
            }
        } else {
            storage = new Storage(path, this, builder);
        }
    }

    public DataBaseHandler<String, String> getDataBase() {
        return storage.getCurrent();
    }

    public Storage getStorage() {
        return storage;
    }
}
