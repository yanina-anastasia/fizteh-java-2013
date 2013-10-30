package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Executor;

public class MultiFileHashMapExecutor extends Executor {

    public MultiFileHashMapExecutor() {

        list();
    }

    @Override
    public void list() {

        Command create = new CmdCreate();
        mapOfCmd.put(create.getName(), create);
        Command drop = new CmdDrop();
        mapOfCmd.put(drop.getName(), drop);
        Command use = new CmdUse();
        mapOfCmd.put(use.getName(), use);
        Command exit = new MultiFileHashMapExit();
        mapOfCmd.put(exit.getName(), exit);
        Command get = new MultiFileHashMapGet();
        mapOfCmd.put(get.getName(), get);
        Command put = new MultiFileHashMapPut();
        mapOfCmd.put(put.getName(), put);
        Command remove = new MultiFileHashMapRemove();
        mapOfCmd.put(remove.getName(), remove);
    }
}
