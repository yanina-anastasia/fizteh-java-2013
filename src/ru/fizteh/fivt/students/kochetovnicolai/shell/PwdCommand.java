package ru.fizteh.fivt.students.kochetovnicolai.shell;

public class PwdCommand implements Executable {

    private FileManager manager;

    public PwdCommand(FileManager fileManager) {
        manager = fileManager;
    }

    @Override
    public String name() {
        return "pwd";
    }

    @Override
    public int argumentsNumber() {
        return 1;
    }

    @Override
    public boolean execute(String[] args) {
        manager.printMessage(manager.getCurrentPath().getAbsolutePath());
        return true;
    }
}
