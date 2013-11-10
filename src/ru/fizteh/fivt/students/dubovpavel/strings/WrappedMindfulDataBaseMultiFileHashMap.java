package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;

import java.io.File;

public class WrappedMindfulDataBaseMultiFileHashMap extends MindfulDataBaseMultiFileHashMap implements Table {
    Dispatcher dispatcher;

    public WrappedMindfulDataBaseMultiFileHashMap(File path, Dispatcher dispatcher) {
        super(path);
        this.dispatcher = dispatcher;
    }
    @Override
    public String get(String key) {
        if(key == null) {
            throw new IllegalArgumentException();
        }
        return super.get(key);
    }
    @Override
    public String put(String key, String value) {
        if(key == null || value == null || key.isEmpty() || key.contains("\n") || value.contains("\n")) {
            throw new IllegalArgumentException();
        }
        return super.put(key, value);
    }
    @Override
    public String remove(String key) {
        if(key == null) {
            throw new IllegalArgumentException();
        }
        return super.remove(key);
    }
    @Override
    public int commit() {
        try {
            return super.commit();
        } catch (DataBaseException e) {
            dispatcher.callbackWriter(Dispatcher.MessageType.ERROR,
                    String.format("Database %s: %s", getName(), e.getMessage()));
        }
        return 0;
    }
}
