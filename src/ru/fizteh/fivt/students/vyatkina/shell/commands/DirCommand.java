package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.State;

import java.util.concurrent.ExecutionException;

public class DirCommand extends AbstractCommand<State> {

    public DirCommand (State state) {
        super (state);
        this.name = "dir";
        this.argsCount = 0;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        try {
            String[] files = state.getFileManager ().getSortedCurrentDirectoryFiles ();
            for (String file : files) {
                state.getIoStreams ().out.println (file);
            }
        }
        catch (RuntimeException e) {
            throw new ExecutionException (e.fillInStackTrace ());
        }
    }
}

