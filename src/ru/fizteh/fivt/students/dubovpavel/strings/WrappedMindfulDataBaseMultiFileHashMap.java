package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DataBaseHandler;

import java.io.File;
import java.util.regex.Pattern;

public class WrappedMindfulDataBaseMultiFileHashMap<V> extends MindfulDataBaseMultiFileHashMap<V> {
    private Dispatcher dispatcher;
    private static final Pattern whitespacePattern;
    static {
        whitespacePattern = Pattern.compile("\\s");
    }

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
        if(key == null || key.isEmpty() || whitespacePattern.matcher(key).find()) {
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
