package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandCreate implements Executable {
    TableManager manager;

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
        if (manager.existsTable(args[1])) {
            manager.printMessage(args[1] + " exists");
            return false;
        }
        try {
            manager.createTable(args[1]);
            manager.printMessage("created");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public TableCommandCreate(TableManager tableManager) {
        manager = tableManager;
    }
}
