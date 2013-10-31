package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DistributedTableProvider implements TableProvider {

    HashMap<String, DistributedTable> tables;
    File currentPath;

    public boolean existsTable(String name) {
        if (name == null || name.equals("..") || name.contains(File.separator)) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        try {
            if (!tables.containsKey(name) && new File(currentPath + File.separator + name).exists()) {
                tables.put(name, new DistributedTable(currentPath, name));
            }
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return tables.containsKey(name);
    }

    protected boolean isValidName(String name) {
        return name != null && !name.contains(".") && !name.equals("")
                && !name.contains(File.separator) && !name.contains(File.pathSeparator);
    }

    public DistributedTableProvider(File workingDirectory) throws IllegalArgumentException {
        currentPath = workingDirectory;
        if (currentPath == null || !currentPath.isDirectory() || (!currentPath.exists()  && !currentPath.mkdir())) {
            throw new IllegalArgumentException("couldn't create working directory");
        }
        tables = new HashMap<>();
    }

    @Override
    public TableMember getTable(String name) throws IllegalArgumentException {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("invalid table name");
        }
        if (tables.containsKey(name)) {
            return new TableMember(tables.get(name), this);
        }
        if (!(new File(currentPath.getPath() + File.separator + name)).exists()) {
            return null;
        }
        return createTable(name);
    }

    @Override
    public TableMember createTable(String name) throws IllegalArgumentException {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("invalid table name");
        }
        if (!tables.containsKey(name)) {
            try {
                DistributedTable table = new DistributedTable(currentPath, name);
                tables.put(name, table);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        return new TableMember(tables.get(name), this);
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("invalid table name");
        }
        if (!(new File(currentPath.getPath() + File.separator + name)).exists()) {
            throw new IllegalStateException("table is not exists");
        }
        if (!tables.containsKey(name)) {
            try {
                getTable(name);
                tables.put(name, new DistributedTable(currentPath, name));
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        try {
            tables.get(name).clear();
            File dir = new File(currentPath.getPath() + File.separator + name);
            if (!dir.delete()) {
                throw new IOException(dir.getPath() + ": couldn't delete directory");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        tables.remove(name);
        if (!new File(currentPath + File.separator + name).delete()) {
            throw new IllegalStateException("couldn't remove table");
        }
    }
}
