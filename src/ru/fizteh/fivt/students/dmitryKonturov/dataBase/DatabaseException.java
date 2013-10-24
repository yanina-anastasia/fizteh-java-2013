package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

/**
 * Throws in work with my databases.
 */

public class DatabaseException extends Exception {
    final String message;
    final String reason;

    DatabaseException(String message, String reason) {
        this.message = message;
        this.reason = reason;
    }

    DatabaseException(String message) {
        this.message = message;
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
