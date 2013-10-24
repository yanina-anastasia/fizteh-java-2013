package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandGet implements Executable {
    TableManager manager;

    @Override
    public String name() {
        return "get";
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
        String value = table.get(args[1]);
        if (value == null) {
            manager.printMessage("not found");
        } else {
            manager.printMessage("found");
            manager.printMessage(value);
        }
        return true;
    }

    public TableCommandGet(TableManager tableManager) {
        manager = tableManager;
    }
}
