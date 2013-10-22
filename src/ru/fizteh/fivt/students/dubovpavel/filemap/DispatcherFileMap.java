package ru.fizteh.fivt.students.dubovpavel.filemap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;

public class DispatcherFileMap extends Dispatcher {
    private final String dbPathProperty = "fizteh.db.dir";
    private DataBase dataBase;

    public DispatcherFileMap(boolean forwarding) {
        super(forwarding);
        String path = System.getProperty(dbPathProperty);
        if(path == null) {
            callbackWriter(MessageType.ERROR, String.format("'%s' property is null", dbPathProperty));
            shutdown = true;
        } else {
            shutdown = false;
            dataBase = new DataBase(path, this);
        }
    }

    public DataBaseHandler<String, String> getDataBase() {
        return dataBase;
    }
}
