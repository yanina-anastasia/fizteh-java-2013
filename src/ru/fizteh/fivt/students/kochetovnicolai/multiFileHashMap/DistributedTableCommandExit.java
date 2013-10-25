package ru.fizteh.fivt.students.kochetovnicolai.multiFileHashMap;

import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class DistributedTableCommandExit implements Executable {

    DistributedTableManager manager;

    @Override
    public String name() {
        return "exit";
    }

    @Override
    public int argumentsNumber() {
        return 1;
    }

    @Override
    public boolean execute(String[] args) {
        DistributedTable table = manager.getCurrentTable();
        if (table.unsavedChanges() != 0) {
            manager.printMessage(table.unsavedChanges() + " unsaved changes");
            return false;
        }
        return true;
    }

    public DistributedTableCommandExit(DistributedTableManager tableManager) {
        manager = tableManager;
    }
}
