package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import java.io.File;
import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.FileSystemOperator;
import ru.fizteh.fivt.students.vishnevskiy.shell.ShellException;

public class Rm implements Command {
    private static final String name = "rm";
    public Rm() {}
    public String getName() {
        return name;
    }

    private void recursivelyRemove(File file) throws ShellException {
        if (file.isDirectory()) {
            for (File innerFile : file.listFiles()) {
                recursivelyRemove(innerFile);
            }
        }
        if(!file.delete()) {
            throw new ShellException("rm: " + file.getPath() + ": failed to delete file or directory");
        }
    }

    public void execute(FileSystemOperator fileSystem, String[] args) throws ShellException {
        if (args.length == 0) {
            throw new ShellException("rm: arguments expected");
        }
        if (args.length > 1) {
            throw new ShellException("rm: wrong number of arguments");
        }

        File file = fileSystem.compileFile(args[0]);
        recursivelyRemove(file);
    }
}
