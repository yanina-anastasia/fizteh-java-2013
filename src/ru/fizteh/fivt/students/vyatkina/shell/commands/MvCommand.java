package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.State;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class MvCommand extends AbstractCommand<State> {

    public MvCommand (State state) {
        super (state);
        this.name = "mv";
        this.argsCount = 2;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        try {
            Path fromPath = Paths.get (args[0]);
            Path toPath = Paths.get (args[1]);
            state.getFileManager ().moveFile (fromPath, toPath);
        }
        catch (IOException | RuntimeException e) {
            throw new ExecutionException (e.fillInStackTrace ());
        }
    }

}
