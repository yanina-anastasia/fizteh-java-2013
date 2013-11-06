package ru.fizteh.fivt.students.dubovpavel.multifilehashmap;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.filemap.DispatcherFileMapBuilder;

public class DispatcherMultiFileHashMapBuilder extends DispatcherFileMapBuilder {
    private DataBaseBuilder builder;
    private boolean pathIsProperty = true;

    public void setDataBaseBuilder(DataBaseBuilder dataBaseBuilder) {
        builder = dataBaseBuilder;
    }

    public void setPathIsProperty(boolean pathIsProperty) {
        this.pathIsProperty = pathIsProperty;
    }

    @Override
    public Dispatcher construct() {
        return setPerformers(new DispatcherMultiFileHashMap(forwarding, pathIsProperty, repo, builder));
    }
}
