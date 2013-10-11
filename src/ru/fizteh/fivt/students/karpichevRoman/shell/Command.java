package ru.fizteh.fivt.students.karpichevRoman.shell;

interface Command {
    boolean isThatCommand(String command);
    void run(Shell shell, String command) throws IllegalArgumentException;
}
