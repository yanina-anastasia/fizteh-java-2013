package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import java.io.File;
import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.FileSystemOperator;
import ru.fizteh.fivt.students.vishnevskiy.shell.ShellException;

public class MkDir implements Command {
    private static final String NAME = "mkdir";
    public MkDir() {}
    public String getName() {
        return NAME;
    }
    public void execute(FileSystemOperator fileSystem, String[] args) throws ShellException {
        if (args.length == 0) {
            throw new ShellException("mkdir: arguments expected");
        }
        if (args.length > 1) {
            throw new ShellException("mkdir: wrong number of arguments");
        }
        File dir = fileSystem.compileFile(args[0]);
        if (!dir.mkdirs()) {
            throw new ShellException("mkdir: " + args[0] + ": failed to create directory");
        }
    }
}

