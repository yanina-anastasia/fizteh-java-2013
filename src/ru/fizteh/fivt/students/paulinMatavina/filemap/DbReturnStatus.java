package ru.fizteh.fivt.students.paulinMatavina.filemap;

class DbReturnStatus extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DbReturnStatus() {
        super();
    }
        
    public DbReturnStatus(String text) {
        super(text);
    }
        
    public DbReturnStatus(int status) {
        super(Integer.toString(status));         
    }
}
