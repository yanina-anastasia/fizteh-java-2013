package ru.fizteh.fivt.students.kochetovnicolai.multiFileHashMap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.kochetovnicolai.fileMap.TableManager;

import java.io.IOException;
import java.util.HashMap;

import java.io.File;

public class DistributedTableManager extends TableManager implements TableProvider {

    HashMap<String, DistributedTable> tables;
    DistributedTable currentTable = null;

    @Override
    public DistributedTable getCurrentTable() {
        return currentTable;
    }

    public boolean existsTable(String name) {
        return (new File(currentPath + File.separator + name).exists());
    }

    public DistributedTableManager(File workingDirectory) throws IOException {
        currentPath = workingDirectory;
        if (!currentPath.exists() && !currentPath.mkdir()) {
            throw new IOException("couldn't create working directory");
        }
        tables = new HashMap<>();
    }

    void setCurrentTable(DistributedTable table) {
        currentTable = table;
        super.currentTable = table;
    }

    public DistributedTable getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        if (!(new File(currentPath.getPath() + File.separator + name)).exists()) {
            return null;
        }
        return createTable(name);
    }

    public DistributedTable createTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        if (!tables.containsKey(name)) {
            try {
                DistributedTable table = new DistributedTable(currentPath, name);
                tables.put(name, table);
            } catch (IOException e) {
                return null;
            }
        }
        return tables.get(name);
    }

    public void removeTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        if (!(new File(currentPath.getPath() + File.separator + name)).exists()) {
            throw new IllegalStateException("table is not exists");
        }
        if (tables.containsKey(name)) {
            tables.remove(name);
        }
        recursiveRemove(new File(currentPath.getPath() + File.separator + name), "table");
    }
}
