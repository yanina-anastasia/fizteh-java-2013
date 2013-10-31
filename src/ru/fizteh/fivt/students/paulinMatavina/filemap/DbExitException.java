package ru.fizteh.fivt.students.paulinMatavina.filemap;

class DbExitException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DbExitException() {
        super();
    }
        
    public DbExitException(String text) {
        super(text);
    }
        
    public DbExitException(int status) {
        super(Integer.toString(status));         
    }
}
