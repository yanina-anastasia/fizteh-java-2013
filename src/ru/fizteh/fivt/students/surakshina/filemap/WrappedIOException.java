package ru.fizteh.fivt.students.surakshina.filemap;

public class WrappedIOException extends RuntimeException {
    public WrappedIOException() {
    }
    public WrappedIOException(String message) {
        super(message);
    }
    public WrappedIOException(String message, Throwable cause) {
        super(message, cause);
    }
    public WrappedIOException(Throwable cause) {
        super(cause);
    }

}
