package ru.fizteh.fivt.students.dmitryKonturov.shell;

public class ShellException extends Exception {
    private final String command;
    private final String message;

    public ShellException(String com, String c) {
        command = com;
        message = c;
    }

    @Override
    public String toString() {
        return (command + ": " + message);
    }
}
