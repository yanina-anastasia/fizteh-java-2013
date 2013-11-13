package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;
import ru.fizteh.fivt.students.dubovpavel.shell2.performers.PerformerRemove;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Storage <DB extends FileRepresentativeDataBase> {
    private File dir;
    protected HashMap<String, DB> storage;
    private DB cursor;
    private Dispatcher dispatcher;
    private DataBaseBuilder<DB> builder;

    public void save() throws StorageException {
        for(Map.Entry<String, DB> entry: storage.entrySet()) {
            try {
                entry.getValue().save();
            } catch(DataBaseHandler.DataBaseException e) {
                throw new StorageException(String.format("Saving: Database %s: %s", entry.getKey(), e.getMessage()));
            }
        }
    }

    public Storage(String path, Dispatcher dispatcher, DataBaseBuilder<DB> dataBaseBuilder) {
        builder = dataBaseBuilder;
        storage = new HashMap<>();
        this.dispatcher = dispatcher;
        dir = new File(path);
        cursor = null;
        if(!dir.isDirectory()) {
            dispatcher.callbackWriter(Dispatcher.MessageType.WARNING,
                    String.format("Storage loading: '%s' is not a directory. Empty storage applied", dir.getPath()));
            dir.mkdirs();
        } else {
            for(File folder: dir.listFiles()) {
                if(folder.isDirectory()) {
                    builder.setPath(folder);
                    DB dataBase = builder.construct();
                    try {
                        dataBase.open();
                    } catch(DataBaseHandler.DataBaseException e) {
                        this.dispatcher.callbackWriter(Dispatcher.MessageType.WARNING,
                                String.format("Storage loading: Database %s: %s", folder.getName(), e.getMessage()));
                    }
                    storage.put(folder.getName(), dataBase);
                }
            }
        }
    }
    public DB create(String key) {
        if(storage.containsKey(key)) {
            dispatcher.callbackWriter(Dispatcher.MessageType.WARNING,
                    String.format("%s exists", key));
            return null;
        } else {
            File newData = new File(dir, key);
            try {
                if(!newData.getCanonicalFile().getName().equals(key)) {
                    dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, "Can not create table with this name");
                    return null;
                }
            } catch(IOException e) {
                throw new RuntimeException(e.getMessage()); // See the note for PerformerShell
            }
            if(!newData.isDirectory()) {
                if(!newData.mkdir()) {
                    dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                            String.format("Can not create directory '%s'", newData.getPath()));
                    return null;
                }
            }
            builder.setPath(newData);
            DB newDataBase = builder.construct();
            storage.put(key, newDataBase);
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, "created");
            return newDataBase;
        }
    }

    public DB drop(String key) {
        if(storage.containsKey(key)) {
            DB value = storage.get(key);
            if(value.getPath().isDirectory()) {
                try {
                    new PerformerRemove().removeObject(storage.get(key).getPath());
                } catch(PerformerRemove.PerformerRemoveException e) {
                    dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, String.format("Drop: Can not remove: %s", e.getMessage()));
                    return null;
                }
            }
            if(cursor != null && cursor.getPath().getName().equals(key)) {
                cursor = null;
            }
            storage.remove(key);
            dispatcher.callbackWriter(Dispatcher.MessageType.SUCCESS, "dropped");
            return value;
        } else {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR, String.format("%s not exists", key));
            return null;
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

    public DB getCurrent() {
        return cursor;
    }

    public DB getDataBase(String name) {
        if(storage.containsKey(name)) {
            return storage.get(name);
        } else {
            return null;
        }
    }
}
