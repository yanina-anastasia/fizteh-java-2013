package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;
import ru.fizteh.fivt.students.dubovpavel.shell2.performers.PerformerRemove;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Storage {
    private File dir;
    private HashMap<String, DataBaseMultiFileHashMap> storage;
    private DataBaseMultiFileHashMap cursor;
    private DispatcherMultiFileHashMap dispatcher;

    public class StorageException extends Exception {
        public StorageException(String msg) {
            super(msg);
        }
    }

    public void save() throws StorageException {
        for(Map.Entry<String, DataBaseMultiFileHashMap> entry: storage.entrySet()) {
            try {
                entry.getValue().save();
            } catch(DataBaseHandler.DataBaseException e) {
                throw new StorageException(String.format("Saving: Database %s: %s", entry.getKey(), e.getMessage()));
            }
        }
    }

    public Storage(String path, DispatcherMultiFileHashMap dispatcherMultiFileHashMap) {
        storage = new HashMap<>();
        dispatcher = dispatcherMultiFileHashMap;
        dir = new File(path);
        cursor = null;
        if(!dir.isDirectory()) {
            dispatcherMultiFileHashMap.callbackWriter(Dispatcher.MessageType.WARNING,
                    String.format("Storage loading: '%s' is not a directory. Empty storage applied", dir.getPath()));
        } else {
            for(File folder: dir.listFiles()) {
                if(folder.isDirectory()) {
                    DataBaseMultiFileHashMap dataBase = new DataBaseMultiFileHashMap(folder);
                    try {
                        dataBase.open();
                    } catch(DataBaseHandler.DataBaseException e) {
                        dispatcher.callbackWriter(Dispatcher.MessageType.WARNING,
                                String.format("Storage loading: Database %s: %s", folder.getName(), e.getMessage()));
                    }
                    storage.put(folder.getName(), dataBase);
                }
            }
        }
    }
    public void create(String key) {
        if(storage.containsKey(key)) {
            dispatcher.callbackWriter(Dispatcher.MessageType.WARNING,
                    String.format("%s exists", key));
        } else {
            storage.put(key, new DataBaseMultiFileHashMap(new File(dir, key)));
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, "created");
        }
    }

    public void drop(String key) {
        if(storage.containsKey(key)) {
            try {
                new PerformerRemove().removeObject(storage.get(key).getPath());
                if(cursor != null && cursor.getPath().getName().equals(key)) {
                    cursor = null;
                }
                storage.remove(key);
                dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, "dropped");
            } catch(PerformerRemove.PerformerRemoveException e) {
                dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, String.format("Drop: %s", e.getMessage()));
            }
        } else {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, String.format("%s not exists", key));
        }
    }

    public void setCurrent(String key) {
        if(storage.containsKey(key)) {
            cursor = storage.get(key);
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS,
                    String.format("using %s", key));
        } else {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("%s not exists", key));
        }
    }

    public DataBaseHandler getCurrent() {
        return cursor;
    }
}
