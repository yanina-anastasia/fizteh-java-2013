package ru.fizteh.fivt.students.elenarykunova.filemap;
import java.io.File;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.storage.strings.TableProvider;


public class FileMapMain implements TableProviderFactory{
        
    public TableProvider create(String dir) throws IllegalArgumentException {
        IllegalArgumentException e = null;
        if (dir == null) {
            e = new IllegalArgumentException("directory is null");
        } else {
            File tmpDir = new File(dir);
            if (!tmpDir.exists()) {
                if (!tmpDir.mkdirs()) {
                    e = new IllegalArgumentException(dir + " doesn't exist and I can't create it");
                }
            } else if (!tmpDir.isDirectory()) {
                e = new IllegalArgumentException(dir + " isn't a directory");
            }            
        }
        if (e != null) {
            throw e;
        }
        return new MyTableProvider(dir);
    }
    
    public static void main(String[] args) {
        FileMapMain myFactory = new FileMapMain();
        MyTableProvider provider = (MyTableProvider) myFactory.create(System.getProperty("fizteh.db.dir"));
        Filemap mp = new Filemap(null, null);
        ExecuteCmd exec = new ExecuteCmd(mp, provider);
        exec.workWithUser(args);
        mp.saveChanges();
    }
}
