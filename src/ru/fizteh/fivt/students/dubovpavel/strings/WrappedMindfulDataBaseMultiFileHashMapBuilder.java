package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DataBaseBuilder;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DispatcherMultiFileHashMap;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.FileRepresentativeDataBase;

public class WrappedMindfulDataBaseMultiFileHashMapBuilder extends DataBaseBuilder<FileRepresentativeDataBase> {
    DispatcherMultiFileHashMap dispatcher;
    public void setDispatcher(DispatcherMultiFileHashMap dispatcherMultiFileHashMap) {
        dispatcher = dispatcherMultiFileHashMap;
    }

    public WrappedMindfulDataBaseMultiFileHashMap construct() {
        assert(dir != null && dispatcher != null);
        return new WrappedMindfulDataBaseMultiFileHashMap(dir, dispatcher);
    }
}
