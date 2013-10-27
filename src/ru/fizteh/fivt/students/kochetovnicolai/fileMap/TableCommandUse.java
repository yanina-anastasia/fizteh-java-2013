package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandUse implements Executable {
    TableManager manager;

    @Override
    public String name() {
        return "use";
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
            if (manager.getCurrentTable() != null) {
                manager.getCurrentTable().commit();
            }
            manager.setCurrentTable(manager.getTable(args[1]));
            manager.printMessage("using " + args[1]);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public TableCommandUse(TableManager tableManager) {
        manager = tableManager;
    }
}
