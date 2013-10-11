package ru.fizteh.fivt.students.vishnevskiy.shell.сommands;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.FileSystemOperator;
import ru.fizteh.fivt.students.vishnevskiy.shell.ShellException;

public class Pwd implements Command{
    private static final String name = "pwd";
    public Pwd() {}
    public String getName() {
        return name;
    }
    public void execute(FileSystemOperator fileSystem, String[] args) throws ShellException {
        if (args.length > 0) {
            throw new ShellException("pwd: no arguments needed");
        }
        System.out.println(fileSystem.getCurrentDirectory());
    }
}
