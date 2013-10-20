package ru.fizteh.fivt.students.paulinMatavina.utils;

public interface Command {
    int execute(String[] args, State state);

    String getName();
    int getArgNum();
    boolean spaceAllowed();
}
