package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.State;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class CdCommand extends AbstractCommand<State> {

    public CdCommand (State state) {
        super (state);
        this.name = "cd";
        this.argsCount = 1;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        Path newDirectory = Paths.get (args[0]);
        try {
            state.getFileManager ().changeCurrentDirectory (newDirectory);
        }
        catch (IOException | RuntimeException e) {
            throw new ExecutionException (e.fillInStackTrace ());
        }
    }

}
