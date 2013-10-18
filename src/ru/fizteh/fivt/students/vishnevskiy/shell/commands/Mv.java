package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.FileSystemOperator;
import ru.fizteh.fivt.students.vishnevskiy.shell.ShellException;

public class Mv implements Command {
    private static final String NAME = "mv";
    public Mv() {}
    public String getName() {
        return NAME;
    }
    public void execute(FileSystemOperator fileSystem, String[] args) throws ShellException {
        if (args.length < 2) {
            throw new ShellException("mv: arguments expected");
        }
        if (args.length > 2) {
            throw new ShellException("mv: wrong number of arguments");
        }
        File source = fileSystem.compileFile(args[0]);
        File target = fileSystem.compileFile(args[1]);
        if (!source.exists()) {
            throw new ShellException("mv: " + source.getName() + ": file or directory doesn't exist");
        }
        if (target.isDirectory()) {
            target = new File(target, source.getName());
        }
        if (target.exists()) {
            throw new ShellException("mv: can't move to " + target.getPath() + ": file or directory already exists");
        }
        try {
            Files.move(source.toPath(), target.toPath());
        } catch (IOException e) {
            throw new ShellException("mv: failed to move " + source.getName() + " to " + target.getPath());
        }
    }
}

