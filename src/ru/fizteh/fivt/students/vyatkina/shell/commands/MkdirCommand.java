package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.State;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class MkdirCommand extends AbstractCommand<State> {

    public MkdirCommand (State state) {
        super (state);
        this.name = "mkdir";
        this.argsCount = 1;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        Path newDir = Paths.get (args[0]);
        try {
            state.getFileManager ().makeDirectory (newDir);
        }
        catch (IOException | RuntimeException e) {
            throw new ExecutionException (e.fillInStackTrace ());
        }

    }
}
