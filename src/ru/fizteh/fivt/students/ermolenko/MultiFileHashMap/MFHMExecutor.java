package ru.fizteh.fivt.students.ermolenko.MultiFileHashMap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Executor;

public class MFHMExecutor extends Executor {

    public MFHMExecutor() {

        list();
    }

    @Override
    public void list() {

        Command create = new Create();
        mapOfCmd.put(create.getName(), create);
        Command drop = new Drop();
        mapOfCmd.put(drop.getName(), drop);
        Command use = new Use();
        mapOfCmd.put(use.getName(), use);
        Command exit = new Exit();
        mapOfCmd.put(exit.getName(), exit);
        Command get = new MFHMGet();
        mapOfCmd.put(get.getName(), get);
        Command put = new MFHMPut();
        mapOfCmd.put(put.getName(), put);
        Command remove = new MFHMRemove();
        mapOfCmd.put(remove.getName(), remove);
    }
}
