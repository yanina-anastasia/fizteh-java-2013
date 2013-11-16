package ru.fizteh.fivt.students.paulinMatavina.filemap;

@SuppressWarnings("serial")
class DbWrongTypeException extends RuntimeException {
    public DbWrongTypeException() {
        super();
    }
        
    public DbWrongTypeException(String text) {
        super(text);
    }
}
