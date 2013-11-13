package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;

import java.io.File;

public class WrappedMindfulDataBaseMultiFileHashMap<V> extends MindfulDataBaseMultiFileHashMap<V> {
    Dispatcher dispatcher;

    public WrappedMindfulDataBaseMultiFileHashMap(File path, Dispatcher dispatcher, ObjectTransformer<V> transformer) {
        super(path, transformer);
        this.dispatcher = dispatcher;
    }
    @Override
    public V get(String key) {
        if(key == null) {
            throw new IllegalArgumentException();
        }
        return super.get(key);
    }
    @Override
    public V put(String key, V value) {
        if(key == null || value == null || key.isEmpty() || key.contains("\n")) {
            throw new IllegalArgumentException();
        }
        return super.put(key, value);
    }
    @Override
    public V remove(String key) {
        if(key == null) {
            throw new IllegalArgumentException();
        }
        return super.remove(key);
    }
    @Override
    public int commit() {
        try {
            return super.commit();
        } catch (DataBaseHandler.DataBaseException e) {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("Database %s: %s", getName(), e.getMessage()));
        }
        return 0;
    }
}
