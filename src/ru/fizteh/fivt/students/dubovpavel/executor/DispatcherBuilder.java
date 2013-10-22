package ru.fizteh.fivt.students.dubovpavel.executor;

import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.Performer;

import java.util.ArrayList;

public abstract class DispatcherBuilder {
    protected boolean forwarding;
    protected ArrayList<Performer> performers;

    public DispatcherBuilder() {
        clear();
    }
    public void setForwarding(boolean forwarding) {
        this.forwarding = forwarding;
    }

    public void addPerformer(Performer performer) {
        performers.add(performer);
    }

    public void clear() {
        forwarding = false;
        performers = new ArrayList<Performer>();
    }

    public abstract Dispatcher construct();
}
