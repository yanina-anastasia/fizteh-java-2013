package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.storage.strings.*;


public class MyTableProviderFactory implements TableProviderFactory {
    public MyTableProviderFactory() { }
    
    public TableProvider create(String dir) {
        return new MyTableProvider(dir);
    }   
}
