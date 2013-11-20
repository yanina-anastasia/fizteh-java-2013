package ru.fizteh.fivt.students.eltyshev.proxy;

public class CyclicReferenceException extends RuntimeException {
    public CyclicReferenceException() {
    }

    public CyclicReferenceException(String message) {
        super(message);
    }

    public CyclicReferenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CyclicReferenceException(Throwable cause) {
        super(cause);
    }

    public CyclicReferenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
