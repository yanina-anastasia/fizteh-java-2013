package ru.fizteh.fivt.students.kamilTalipov.shell;

public interface Command {
    String getName();
    int getNumberOfArguments();
    boolean equalName(String name);
    boolean equalNumberOfArguments(int numberOfArguments);

    void run(Shell shell, String[] args) throws IllegalArgumentException;
}
