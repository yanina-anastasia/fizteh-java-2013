package ru.fizteh.fivt.students.valentinbarishev.shell;

public class InvalidCommandException extends Error {
    public InvalidCommandException(String message) {
        super("Invalid command: " + message);
    }
}
