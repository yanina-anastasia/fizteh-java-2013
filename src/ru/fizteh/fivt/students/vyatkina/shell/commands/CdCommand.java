package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.shell.Command;
import ru.fizteh.fivt.students.vyatkina.shell.FileManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class CdCommand implements Command {

    private final FileManager fileManager;

    public CdCommand (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        Path newDirectory = Paths.get (args [0]);
        try {
        fileManager.changeCurrentDirectory (newDirectory);
        }
        catch (IOException |  RuntimeException e) {
         throw new ExecutionException (e.fillInStackTrace ());
        }
    }

    @Override
    public String getName () {
        return "cd";
    }

    @Override
    public int getArgumentCount () {
        return 1;
    }
}
