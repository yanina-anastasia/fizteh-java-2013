package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.State;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;


public class CpCommand extends AbstractCommand<State> {

    public CpCommand (State state) {
        super (state);
        this.name = "cp";
        this.argsCount = 2;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        Path fromPath = Paths.get (args[0]);
        Path toPath = Paths.get (args[1]);
        try {
            state.getFileManager ().copyFile (fromPath, toPath);
        }
        catch (IOException | RuntimeException e) {
            throw new ExecutionException (e.fillInStackTrace ());
        }
    }

}
