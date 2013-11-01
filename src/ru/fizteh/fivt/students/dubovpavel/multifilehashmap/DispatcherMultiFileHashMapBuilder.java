package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DispatcherFileMapBuilder;

public class DispatcherMultiFileHashMapBuilder extends DispatcherFileMapBuilder {
    private DataBaseBuilder builder;

    public void setBuilder(DataBaseBuilder dataBaseBuilder) {
        builder = dataBaseBuilder;
    }

    @Override
    public Dispatcher construct() {
        return setPerformers(new DispatcherMultiFileHashMap(forwarding, repo, builder));
    }
}
