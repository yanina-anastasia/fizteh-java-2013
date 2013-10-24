package ru.fizteh.fivt.students.dubovpavel.shell2;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.DispatcherBuilder;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;

public class DispatcherShellBuilder extends DispatcherBuilder {
    public Dispatcher construct() {
        return setPerformers(new Dispatcher(forwarding));
    }
}
