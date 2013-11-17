package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.IOException;
import ru.fizteh.fivt.storage.structured.*;

public class MyTableProviderFactory implements TableProviderFactory {
    public MyTableProviderFactory() { }
    
    public TableProvider create(String dir) throws IOException {
        return new MyTableProvider(dir);
    }   
}
