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
    
    public static void main(String[] args) {
        FileMapMain myFactory = new FileMapMain();
        MyTableProvider provider;
        try {
            provider = (MyTableProvider) myFactory.create(System.getProperty("fizteh.db.dir"));
            Filemap mp = new Filemap();
            ExecuteCmd exec = new ExecuteCmd(mp, provider);
            exec.workWithUser(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e1) {
            System.err.println(e1.getMessage());
            System.exit(1);
        } catch (IllegalStateException e2) {
            System.err.println(e2.getMessage());
            System.exit(1);
        } catch (RuntimeException e3) {
            System.err.println(e3.getMessage());
            System.exit(1);
        }
    }
}
