package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DataBaseBuilder;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.FileRepresentativeDataBase;

public class WrappedMindfulDataBaseMultiFileHashMapBuilder extends DataBaseBuilder<FileRepresentativeDataBase> {
    Dispatcher dispatcher;
    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public WrappedMindfulDataBaseMultiFileHashMap construct() {
        assert(dir != null && dispatcher != null);
        return new WrappedMindfulDataBaseMultiFileHashMap(dir, dispatcher);
    }
}
