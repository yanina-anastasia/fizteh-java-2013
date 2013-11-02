package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellUtility;

public class FileMapProvider implements TableProvider {
    private Path rootDir;

    @Override
    public FileMap getTable(String name) {
        Path dbPath = Paths.get(rootDir.normalize() + "/" + name);
        if (dbPath == null || name.contains("/")) {
            throw new IllegalArgumentException("Invalid path");
        }
        try {
            return IOUtility.parseDatabase(dbPath);
        } 
        catch (IOException e) {
            return null;
        }
    }

    @Override
    public FileMap createTable(String name) {
        Path dbPath = Paths.get(rootDir.normalize() + "/" + name);
        if (dbPath == null || name.contains("/")) {
            throw new IllegalArgumentException("Invalid path");
        }
        if (dbPath.toFile().exists()) {
            return null;
        }
        dbPath.toFile().mkdir();
        return new FileMap(name);
    }

    @Override
    public void removeTable(String name) {
        Path dbPath = Paths.get(rootDir.normalize() + "/" + name);
        if (dbPath == null || name.contains("/")) {
            throw new IllegalArgumentException("Invalid path");
        }
        try {
            ShellUtility.removeDir(dbPath);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
    
    public String getRootDir() {
        return new String(rootDir.toString());
    }

    public FileMapProvider(String root) throws IllegalStateException {
        rootDir = Paths.get(root);
        if (rootDir == null || !rootDir.toFile().exists() || !rootDir.toFile().isDirectory()) {
            throw new IllegalStateException("fizteh.db.dir did not resolve to a valid directory");
        }
    }
}
