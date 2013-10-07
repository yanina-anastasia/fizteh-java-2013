package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class CdCommand implements Executable {

    private FileManager manager;

    public CdCommand(FileManager fileManager) {
        manager = fileManager;
    }

    @Override
    public String name() {
        return "cd";
    }

    @Override
    public int argumentsNumber() {
        return 2;
    }

    @Override
    public boolean execute(String args[]) {
        File newPath = manager.resolvePath(args[1]);
        if (newPath == null || !newPath.exists()) {
            manager.printMessage(args[0] + ": \'" + args[1] + "\': No such file or directory");
        } else if (!newPath.isDirectory()) {
            manager.printMessage(args[0] + ": \'" + args[1] + "': expected directory name, but file found");
        } else {
            return manager.setCurrentPath(newPath);
        }
        return false;
    }
}
