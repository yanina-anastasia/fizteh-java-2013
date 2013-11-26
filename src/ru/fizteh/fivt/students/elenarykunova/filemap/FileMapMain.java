package ru.fizteh.fivt.students.elenarykunova.filemap;
import java.io.File;
import java.io.IOException;


import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.storage.structured.TableProvider;


public class FileMapMain implements TableProviderFactory {
        
    public TableProvider create(String dir) throws IllegalArgumentException, IOException {
        if (dir == null || dir.isEmpty() || dir.trim().isEmpty()) {
            throw new IllegalArgumentException("directory is not set");
        } else {
            File tmpDir = new File(dir);
            if (!tmpDir.exists()) {
                if (!tmpDir.mkdirs()) {
                    throw new IOException(dir + " doesn't exist and I can't create it");
                }
            } else if (!tmpDir.isDirectory()) {
                throw new IllegalArgumentException(dir + " isn't a directory");
            }            
        }
        MyTableProvider prov = null;
        prov = new MyTableProvider(dir);
        return (TableProvider) prov;
    }    
}
