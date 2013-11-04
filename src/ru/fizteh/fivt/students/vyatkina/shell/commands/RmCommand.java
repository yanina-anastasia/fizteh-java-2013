package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.State;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class RmCommand extends AbstractCommand<State> {

    public RmCommand (State state) {
        super (state);
        this.name = "rm";
        this.argsCount = 1;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        try {
            Path file = Paths.get (args[0]);
            state.getFileManager ().deleteFile (file);
        }
        catch (IOException | RuntimeException e) {
            throw new ExecutionException (e.fillInStackTrace ());
        }
    }
}
