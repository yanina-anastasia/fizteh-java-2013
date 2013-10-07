package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class RmCommand implements Executable {

    private FileManager manager;

    public RmCommand(FileManager fileManager) {
        manager = fileManager;
    }

    @Override
    public String name() {
        return "rm";
    }

    @Override
    public int argumentsNumber() {
        return 2;
    }

    @Override
    public boolean execute(String args[]) {
        File files[] = manager.getCurrentPath().listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(args[1])) {
                    return manager.recursiveRemove(file, args[0]);
                }
            }
        }
        manager.printMessage(args[0] + ": cannot remove \'" + args[1] + "\': No such file or directory");
        return false;
    }
}
