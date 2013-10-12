package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.FileSystemOperator;
import ru.fizteh.fivt.students.vishnevskiy.shell.ShellException;

public class Pwd implements Command{
    private static final String NAME = "pwd";
    public Pwd() {}
    public String getName() {
        return NAME;
    }
    public void execute(FileSystemOperator fileSystem, String[] args) throws ShellException {
        if (args.length > 0) {
            throw new ShellException("pwd: no arguments needed");
        }
        System.out.println(fileSystem.getCurrentDirectory());
    }
}
