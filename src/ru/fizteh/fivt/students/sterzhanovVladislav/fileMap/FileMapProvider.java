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
        Path dbPath = Paths.get(rootDir.normalize() + "/" + name);
        if (dbPath == null || name.contains("/")) {
            throw new IllegalArgumentException("Invalid path");
        }
        try {
            FileMap table = tables.get(name);
            if (table == null) {
                table = IOUtility.parseDatabase(dbPath);
                tables.put(name, table);
            }
            return table;
        } 
        catch (IOException e) {
            return null;
        }
    }

    @Override
    public FileMap createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        Path dbPath = Paths.get(rootDir.normalize() + "/" + name);
        if (dbPath == null || name.contains("/") || name.isEmpty()) {
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
        Path dbPath = Paths.get(rootDir.normalize() + "/" + name);
        if (dbPath == null || name.contains("/")) {
            throw new IllegalArgumentException("Invalid path");
        }
        try {
            ShellUtility.removeDir(dbPath);
            tables.remove(name);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
    
    public String getRootDir() {
        return new String(rootDir.toString());
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
}
