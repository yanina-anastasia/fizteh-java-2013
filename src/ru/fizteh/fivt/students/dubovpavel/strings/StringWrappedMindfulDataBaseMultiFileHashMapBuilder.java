package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DataBaseBuilder;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.FileRepresentativeDataBase;

public class StringWrappedMindfulDataBaseMultiFileHashMapBuilder extends WrappedMindfulDataBaseMultiFileHashMapBuilder<String> {
    public StringWrappedMindfulDataBaseMultiFileHashMap construct() {
        assert(dir != null && dispatcher != null);
        return new StringWrappedMindfulDataBaseMultiFileHashMap(dir, dispatcher);
    }
}
