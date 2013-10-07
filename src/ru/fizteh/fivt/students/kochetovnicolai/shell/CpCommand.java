package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class CpCommand implements Executable {

    private FileManager manager;

    public CpCommand(FileManager fileManager) {
        manager = fileManager;
    }

    @Override
    public String name() {
        return "cp";
    }

    @Override
    public int argumentsNumber() {
        return 3;
    }

    @Override
    public boolean execute(String args[]) {
        File source = manager.resolvePath(args[1]);
        File destination = manager.resolvePath(args[2]);
        return manager.safeCopy(source, destination, name());
    }
}