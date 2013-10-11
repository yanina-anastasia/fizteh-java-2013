package ru.fizteh.fivt.students.vishnevskiy.shell;

public class ShellException extends Exception {
    private String message;
    public ShellException(String e) {
        message = e;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
