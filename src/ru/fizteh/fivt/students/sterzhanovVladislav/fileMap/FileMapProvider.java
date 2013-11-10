package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellUtility;

public class FileMapProvider implements TableProvider {
    private Path rootDir;
    private HashMap<String, FileMap> tables;

    @Override
    public FileMap getTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Path dbPath = Paths.get(rootDir.normalize() + "/" + name);
        if (dbPath == null || !isValidFileName(name)) {
            throw new IllegalArgumentException("Invalid path");
        }
        try {
            FileMap table = tables.get(name);
            if (table == null) {
                table = IOUtility.parseDatabase(dbPath);
                tables.put(name, table);
            }
            return table; 
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public FileMap createTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Path dbPath = Paths.get(rootDir.normalize() + "/" + name);
        if (dbPath == null || !isValidFileName(name)) {
            throw new IllegalArgumentException("Invalid path");
        }
        if (dbPath.toFile().exists()) {
            return null;
        }
        dbPath.toFile().mkdir();
        FileMap newFileMap = new FileMap(name);
        tables.put(name, newFileMap);
        return newFileMap;
    }

    @Override
    public void removeTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Path dbPath = Paths.get(rootDir.normalize() + "/" + name);
        if (dbPath == null || !isValidFileName(name)) {
            throw new IllegalArgumentException("Invalid path");
        }
        try {
            ShellUtility.removeDir(dbPath);
            tables.remove(name);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
    
    public void removeAllTables() {
        for (String tableName : tables.keySet()) {
            removeTable(tableName);
        }
    }
    
    public String getRootDir() {
        return rootDir.toString();
    }

    public FileMapProvider(String root) throws IllegalArgumentException {
        if (root == null) {
            throw new IllegalArgumentException();
        }
        rootDir = Paths.get(root);
        if (rootDir == null || !rootDir.toFile().exists() || !rootDir.toFile().isDirectory()) {
            throw new IllegalArgumentException("fizteh.db.dir did not resolve to a valid directory");
        }
        tables = new HashMap<String, FileMap>();
    }
    
    private static boolean isValidFileName(String name) {
        return !(name.contains("\\") || name.contains("/")
                || name.contains(":") || name.contains("*")
                || name.contains("?") || name.contains("\"")
                || name.contains("<") || name.contains(">")
                || name.contains("\n") || name.contains(" ")
                || name.contains("|") || name.contains("\t"));
    }
}
