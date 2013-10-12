package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.FileSystemOperator;
import ru.fizteh.fivt.students.vishnevskiy.shell.ShellException;

public class Cp implements Command {
    private static final String NAME = "cp";
    public Cp() {}
    public String getName() {
        return NAME;
    }
    public void execute(FileSystemOperator fileSystem, String[] args) throws ShellException {
        if (args.length < 2) {
            throw new ShellException("cp: arguments expected");
        }
        if (args.length > 2) {
            throw new ShellException("cp: wrong number of arguments");
        }
        File source = fileSystem.compileFile(args[0]);
        File target = fileSystem.compileFile(args[1]);
        if (!source.exists()) {
            throw new ShellException("cp: " + source.getName() + ": file or directory doesn't exist");
        }
        if (target.isDirectory()) {
            target = new File(target, source.getName());
        }
        if (target.exists()) {
            throw new ShellException("cp: can't copy to " + target.getPath() + ": file or directory already exists");
        }
        try {
            Files.copy(source.toPath(), target.toPath());
        } catch (IOException e) {
            throw new ShellException("cp: failed to copy " + source.getName() + " to " + target.getPath());
        }
    }
}

