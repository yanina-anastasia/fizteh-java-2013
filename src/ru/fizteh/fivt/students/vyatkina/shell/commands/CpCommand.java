package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.shell.Command;
import ru.fizteh.fivt.students.vyatkina.shell.FileManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;


public class CpCommand implements Command {

    private final FileManager fileManager;

    public CpCommand (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        Path fromPath = Paths.get (args[0]);
        Path toPath = Paths.get (args[1]);
        try {
        fileManager.copyFile (fromPath, toPath);
        }
        catch (IOException | RuntimeException e) {
            throw new ExecutionException (e.fillInStackTrace ());
        }
    }

    @Override
    public String getName () {
        return "cp";
    }

    @Override
    public int getArgumentCount () {
        return 2;
    }
}
