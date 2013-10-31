package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.File;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell.ExitCode;

public class MyTableProvider implements TableProvider {

    private String rootDir = null;

    public MyTableProvider() {
    }

    public MyTableProvider(String newRootDir) throws IllegalArgumentException {
        IllegalArgumentException e = null;
        if (newRootDir == null || newRootDir.isEmpty() || newRootDir.trim().isEmpty()) {
            e = new IllegalArgumentException("directory is null");
        } else {
            File tmpDir = new File(newRootDir);
            if (!tmpDir.exists()) {
                if (!tmpDir.mkdirs()) {
                    e = new IllegalArgumentException(newRootDir + " doesn't exist and I can't create it");
                }
            } else if (!tmpDir.isDirectory()) {
                e = new IllegalArgumentException(newRootDir + " isn't a directory");
            }            
        }
        if (e != null) {
            throw e;
        }
        rootDir = newRootDir;
    }

    public String getPath(String tableName) {
        if (rootDir == null) {
            return null;
        }
        return rootDir + File.separator + tableName;
    }

    public boolean isEmpty(String str) {
        return (str == null || str.isEmpty() || str.trim().isEmpty());
    }

    public boolean hasBadSymbols(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\' || c == '/' || c == '.' || c == ':' || c == '*'
                    || c == '?' || c == '|' || c == '"' || c == '<' || c == '>') {
                return true;
            }
        }
        return false;
    }

    public Table getTable(String name) throws IllegalArgumentException,
            RuntimeException {
        if (isEmpty(name)) {
            throw new IllegalArgumentException("name of table is empty");
        }
        if (hasBadSymbols(name)) {
            throw new RuntimeException("name of table contains bad symbol");
        }
        String tablePath = getPath(name);
        if (tablePath == null) {
            throw new RuntimeException("no root directory");
        }
        File tmpFile = new File(tablePath);
        if (!tmpFile.exists() || !tmpFile.isDirectory()) {
            return null;
        }
        try {
            return new Filemap(tablePath, name);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public Table createTable(String name) throws IllegalArgumentException,
            RuntimeException {
        if (isEmpty(name)) {
            throw new IllegalArgumentException("name of table is empty");
        }
        if (hasBadSymbols(name)) {
            throw new RuntimeException("name of table contains bad symbol");
        }
        String tablePath = getPath(name);
        if (tablePath == null) {
            throw new RuntimeException("no root directory");
        }
        File tmpFile = new File(tablePath);
        if (tmpFile.exists() && tmpFile.isDirectory()) {
            return null;
        } else {
            Shell sh = new Shell(rootDir);
            if (sh.mkdir(name) == ExitCode.OK) {
                try {
                    return new Filemap(tablePath, name);
                } catch (RuntimeException e) {
                    throw e;
                }
            } else {
                throw new RuntimeException(name + " can't create a table");
            }
        }
    }

    public void removeTable(String name) throws RuntimeException,
            IllegalArgumentException, IllegalStateException {
        if (isEmpty(name)) {
            throw new IllegalArgumentException("name of table is empty");
        }
        if (hasBadSymbols(name)) {
            throw new RuntimeException("name of table contains bad symbol");
        }
        String tablePath = getPath(name);
        if (tablePath == null) {
            throw new RuntimeException("no root directory");
        }
        File tmpFile = new File(tablePath);
        if (!tmpFile.exists() || !tmpFile.isDirectory()) { 
            throw new IllegalStateException(name + " not exists");
        } else {
            Shell sh = new Shell(rootDir);
            if (sh.rm(name) == ExitCode.OK) {
                return;
            } else {
                throw new RuntimeException(name + " can't remove table");
            }
        }
    }
}
