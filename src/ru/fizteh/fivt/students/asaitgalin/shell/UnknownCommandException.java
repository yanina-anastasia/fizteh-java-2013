package ru.fizteh.fivt.students.asaitgalin.shell;

public class UnknownCommandException extends Exception {

    public UnknownCommandException(String command) {
        super("Unrecognized command: \"" + command + "\"");
    }

}
