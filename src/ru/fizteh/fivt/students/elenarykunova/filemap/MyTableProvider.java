package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.File;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell.ExitCode;

public class MyTableProvider implements TableProvider{
    
    private String rootDir;
    
    public MyTableProvider(String newRootDir) {
        rootDir = newRootDir;
    }
    
    public String getPath(String tableName) {
        return rootDir + File.separator + tableName;
    }
    
    public Table getTable(String name) throws IllegalArgumentException {
        IllegalArgumentException e = checkName(name);
        if (e != null) {
            throw e;
        }
        String tablePath = getPath(name);
        File tmpFile = new File(tablePath);
        if (!tmpFile.exists() || !tmpFile.isDirectory()) {
            return null;
        }
        return new Filemap(tablePath, name);        
    }

    public Table createTable(String name) throws IllegalArgumentException {
        IllegalArgumentException e = checkName(name);
        if (e != null) {
            throw e;
        }
        String tablePath = getPath(name);
        File tmpFile = new File(tablePath);
        if (tmpFile.exists() && tmpFile.isDirectory()) {
            return null;
        } else {
            Shell sh = new Shell(rootDir);
            if (sh.mkdir(name) == ExitCode.OK) {
                return new Filemap(tablePath, name);
            } else {
                RuntimeException e3 = new RuntimeException();
                throw e3;
            }
        }
    }

    public IllegalArgumentException checkName(String name) {
        IllegalArgumentException e = null;
        if (name == null) {
            e = new IllegalArgumentException("no name");
        } else {
            if (name.contains(".") || name.contains(File.separator) || name.contains(";")) {
                e = new IllegalArgumentException("illegal name");
            }
        }
        return e;
    }
    
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        IllegalArgumentException e = checkName(name);
        if (e != null) {
            throw e;
        }
        String tablePath = getPath(name);
        File tmpFile = new File(tablePath);
        if (!tmpFile.exists() || !tmpFile.isDirectory()) {
            IllegalStateException e2 = new IllegalStateException();
            throw e2;
        } else {
            Shell sh = new Shell(rootDir);
            if (sh.rm(name) == ExitCode.OK) {
                return;
            } else {
                RuntimeException e3 = new RuntimeException();
                throw e3;
            }
        }
    }
}
