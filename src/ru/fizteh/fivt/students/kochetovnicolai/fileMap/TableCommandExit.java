package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandExit implements Executable {
    TableManager manager;

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
        manager.setExit();
        return true;
    }

    public TableCommandExit(TableManager tableManager) {
        manager = tableManager;
    }
}
