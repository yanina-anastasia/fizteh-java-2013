package ru.fizteh.fivt.students.dubovpavel.filemap;

import ru.fizteh.fivt.students.dubovpavel.filemap.Performers.*;
import ru.fizteh.fivt.students.dubovpavel.executor.Feeder;

public class Main {
    public static void main(String[] args) {
        DispatcherFileMapBuilder dispatcherFileMapBuilder = new DispatcherFileMapBuilder();
        dispatcherFileMapBuilder.addPerformer(new PerformerExit());
        dispatcherFileMapBuilder.addPerformer(new PerformerGet());
        dispatcherFileMapBuilder.addPerformer(new PerformerHalt());
        dispatcherFileMapBuilder.addPerformer(new PerformerPut());
        dispatcherFileMapBuilder.addPerformer(new PerformerRemove());
        Feeder.feed(dispatcherFileMapBuilder, args);
    }
}
