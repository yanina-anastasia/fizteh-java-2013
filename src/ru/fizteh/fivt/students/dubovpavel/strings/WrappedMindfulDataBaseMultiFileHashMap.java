package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DispatcherMultiFileHashMap;

import java.io.File;

public class WrappedMindfulDataBaseMultiFileHashMap extends MindfulDataBaseMultiFileHashMap implements Table {
    DispatcherMultiFileHashMap dispatcher;

    public WrappedMindfulDataBaseMultiFileHashMap(File path, DispatcherMultiFileHashMap dispatcherMultiFileHashMap) {
        super(path);
        dispatcher = dispatcherMultiFileHashMap;
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
