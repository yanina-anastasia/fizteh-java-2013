package ru.fizteh.fivt.students.mescherinilya.filemap;

public class IncorrectFileFormatException extends Exception {
    public IncorrectFileFormatException() {
        super();
    }

    public IncorrectFileFormatException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
