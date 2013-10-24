package ru.fizteh.fivt.students.dubovpavel.filemap;


import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.DispatcherBuilder;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;

public class DispatcherFileMapBuilder extends DispatcherBuilder {
    protected String repo;
    public Dispatcher construct() {
        return setPerformers(new DispatcherFileMap(forwarding, repo));
    }

    public void setRepoPath(String path) {
        repo = path;
    }
}
