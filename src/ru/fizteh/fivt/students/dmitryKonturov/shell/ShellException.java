package ru.fizteh.fivt.students.dmitryKonturov.shell;

public class ShellException extends Exception {
    private final String message;
    private final String reason;

    public ShellException(String theMessage, String theReason) {
        this.message = theMessage;
        this.reason = theReason;
    }

    @Override
    public String getMessage() {
        if (reason != null) {
            return message + ": " + reason;
        } else {
            return message;
        }
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
