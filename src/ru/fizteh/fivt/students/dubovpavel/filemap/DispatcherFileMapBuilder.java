package ru.fizteh.fivt.students.dubovpavel.filemap;


import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.DispatcherBuilder;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;

public class DispatcherFileMapBuilder extends DispatcherBuilder {
    public Dispatcher construct() {
        Dispatcher dispatcher = new DispatcherFileMap(forwarding);
        for(Performer performer : performers) {
            dispatcher.addPerformer(performer);
        }
        return dispatcher;
    }
}
