package ru.fizteh.fivt.students.kochetovnicolai.multiFileHashMap;

import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class DistributedTableCommandDrop implements Executable {
    DistributedTableManager manager;

    @Override
    public String name() {
        return "create";
    }

    @Override
    public int argumentsNumber() {
        return 2;
    }

    @Override
    public boolean execute(String[] args) {
        if (!manager.existsTable(args[1])) {
            manager.printMessage(args[1] + " not exists");
            return false;
        }
        try {
            manager.removeTable(args[1]);
            manager.printMessage("dropped");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public DistributedTableCommandDrop(DistributedTableManager tableManager) {
        manager = tableManager;
    }
}
