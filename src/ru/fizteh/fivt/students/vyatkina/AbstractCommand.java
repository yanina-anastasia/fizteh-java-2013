package ru.fizteh.fivt.students.vyatkina;

import java.util.concurrent.ExecutionException;

abstract public class AbstractCommand<State> implements Command {

    protected String name;
    protected int argsCount;
    protected State state;

    public AbstractCommand (State state) {
        this.state = state;
    }

    @Override
    abstract public void execute (String[] args) throws ExecutionException;

    @Override
    public String getName () {
        return name;
    }

    @Override
    public int getArgumentCount () {
        return argsCount;
    }
}
