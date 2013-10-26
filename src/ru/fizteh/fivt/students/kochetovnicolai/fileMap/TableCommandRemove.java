package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandRemove implements Executable {
    TableManager manager;

    @Override
    public String name() {
        return "remove";
    }

    @Override
    public int argumentsNumber() {
        return 2;
    }

    @Override
    public boolean execute(String[] args) {
        Table table = manager.getCurrentTable();
        if (table == null) {
            manager.printMessage("no table");
            return false;
        }
        String oldValue = table.remove(args[1]);
        if (oldValue == null) {
            manager.printMessage("not found");
        } else {
            manager.printMessage("removed");
        }
        return true;
    }

    public TableCommandRemove(TableManager tableManager) {
        manager = tableManager;
    }
}
