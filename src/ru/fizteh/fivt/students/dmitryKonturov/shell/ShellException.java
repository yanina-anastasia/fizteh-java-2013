package ru.fizteh.fivt.students.dmitryKonturov.shell;

public class ShellException extends Exception {

    private static String getUnitedMessage(String message, String reason) {
        if (message == null) {
            message = reason;
            reason = null;
        }
        if (reason == null) {
            if (message == null) {
                return "Database Exception";
            } else {
                return message;
            }
        } else {
            return message + ": " + reason;
        }
    }

    @Override
    public String toString() {
        String current = super.toString();
        String[] strings = current.split(":", 2);
        if (strings.length > 1) {
            if (strings[1] != null) {
                if (!strings[1].equals("")) {
                    return strings[1];
                }
            }
        }
        return current;
    }

    public ShellException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ShellException(String message, String reason) {
        super(getUnitedMessage(message, reason));
    }

    public ShellException(String message) {
        super(getUnitedMessage(message, null));
    }

    public ShellException() {
        super();
    }

    public ShellException(Throwable throwable) {
        super(throwable);
    }
}
