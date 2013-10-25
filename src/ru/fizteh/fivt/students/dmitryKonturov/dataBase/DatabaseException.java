package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

/**
 * Throws in work with my databases.
 */

public class DatabaseException extends Exception {
    final private String message;
    final private String reason;

    DatabaseException(String msg, String rsn) {
        this.message = msg;
        this.reason = rsn;
    }

    DatabaseException(String msg) {
        this.message = msg;
        this.reason = null;
    }

    DatabaseException() {
        this.message = null;
        this.reason = null;
    }

    @Override
    public String toString() {
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
}
