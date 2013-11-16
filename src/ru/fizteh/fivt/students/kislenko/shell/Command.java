package ru.fizteh.fivt.students.kislenko.shell;

public interface Command<State> {
    public String getName();

    public int getArgCount();

    public void run(State state, String[] args) throws Exception;
}