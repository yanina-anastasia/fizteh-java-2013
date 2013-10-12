package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import java.io.File;
import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.FileSystemOperator;
import ru.fizteh.fivt.students.vishnevskiy.shell.ShellException;

public class Dir implements Command {
    private static final String name = "dir";
    public Dir() {}
    public String getName() {
        return name;
    }
    public void execute(FileSystemOperator fileSystem, String[] args) throws ShellException {
        if (args.length > 0) {
            throw new ShellException("dir: no arguments needed");
        }
        File currentDir = new File(fileSystem.getCurrentDirectory());
        String[] files = currentDir.list();
        for (String file : files) {
            System.out.println(file);
        }
    }
}
