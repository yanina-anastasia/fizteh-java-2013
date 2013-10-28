package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.shell.Command;
import ru.fizteh.fivt.students.vyatkina.shell.FileManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class MvCommand implements Command {

    private final FileManager fileManager;

    public MvCommand (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
    try {
    Path fromPath = Paths.get (args[0]);
    Path toPath = Paths.get (args[1]);
    fileManager.moveFile (fromPath,toPath);
    } catch (IOException | RuntimeException e) {
        throw new ExecutionException (e.fillInStackTrace ());
    }
    }

    @Override
    public String getName () {
        return "mv";
    }

    @Override
    public int getArgumentCount () {
        return 2;
    }
}
