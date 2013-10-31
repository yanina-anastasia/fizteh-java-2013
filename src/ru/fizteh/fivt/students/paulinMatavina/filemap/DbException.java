package ru.fizteh.fivt.students.paulinMatavina.filemap;

class DbException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DbException() {
        super();
    }
        
    public DbException(String text) {
        super(text);
    }
        
    public DbException(int status) {
        super(Integer.toString(status));         
    }
}
