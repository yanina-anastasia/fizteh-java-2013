package ru.fizteh.fivt.students.kochetovnicolai.shell;

public class ExitCommand implements Executable {

    private FileManager manager;

    public ExitCommand(FileManager fileManager) {
        manager = fileManager;
    }

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
}
