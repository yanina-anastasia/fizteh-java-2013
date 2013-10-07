package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class MkdirCommand implements Executable {

    private FileManager manager;

    public MkdirCommand(FileManager fileManager) {
        manager = fileManager;
    }

    @Override
    public String name() {
        return "mkdir";
    }

    @Override
    public int argumentsNumber() {
        return 2;
    }

    @Override
    public boolean execute(String args[]) {
        try {
            File newDirectory = new File(manager.getCurrentPath().getAbsolutePath() + File.separator + args[1]);
            if (newDirectory.exists()) {
                manager.printMessage(args[0] + ": \'" + args[1] + "\': directory already exists");
                return false;
            } else if (!newDirectory.mkdir()) {
                manager.printMessage(args[0] + ": \'" + args[1] + "\': couldn't create directory");
            }
            return true;
        } catch (SecurityException e) {
            manager.printMessage(args[0] + ": \'" + args[1] + "\': couldn't create directory");
        }
        return false;
    }
}
