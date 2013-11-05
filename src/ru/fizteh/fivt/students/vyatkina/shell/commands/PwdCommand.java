package ru.fizteh.fivt.students.vyatkina.shell.commands;


import ru.fizteh.fivt.students.vyatkina.AbstractCommand;
import ru.fizteh.fivt.students.vyatkina.State;

import java.util.concurrent.ExecutionException;

public class PwdCommand extends AbstractCommand<State> {

    public PwdCommand (State state) {
        super (state);
        this.name = "pwd";
        this.argsCount = 0;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
        try {
            state.getIoStreams ().out.println (state.getFileManager ().getCurrentDirectoryString ());
        }
        catch (RuntimeException e) {
            throw new ExecutionException (e.fillInStackTrace ());
        }
    }

}
