package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.FileSystemOperator;
import ru.fizteh.fivt.students.vishnevskiy.shell.ShellException;

public class Cd implements Command {
    private static final String NAME = "cd";
    public Cd() {}
    public String getName() {
        return NAME;
    }
    public void execute(FileSystemOperator fileSystem, String[] args) throws ShellException {
        if (args.length == 0) {
            throw new ShellException("cd: arguments expected");
        }
        if (args.length > 1) {
            throw new ShellException("cd: wrong number of arguments");
        }

        fileSystem.changeCurrentDirectory(args[0]);
    }
}
