package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.kochetovnicolai.shell.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class TableManager extends FileManager implements TableProvider {

    protected DistributedTable currentTable;

    HashMap<String, DistributedTable> tables;

    public boolean existsTable(String name) {
        return (new File(currentPath + File.separator + name).exists());
    }

    public TableManager(File workingDirectory) throws IOException {
        currentPath = workingDirectory;
        if (!currentPath.exists() && !currentPath.mkdir()) {
            throw new IOException("couldn't create working directory");
        }
        tables = new HashMap<>();
    }

    void setCurrentTable(DistributedTable table) {
        currentTable = table;
    }

    @Override
    public DistributedTable getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("table name shouldn't be null");
        }
        if (!(new File(currentPath.getPath() + File.separator + name)).exists()) {
            return null;
        }
        return createTable(name);
    }

    @Override
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

    @Override
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

    @Override
    public void setExit() {
        if (currentTable != null) {
            currentTable.commit();
        }
        super.setExit();
    }

    public Table getCurrentTable() {
        return currentTable;
    }

    @Override
    public void printSuggestMessage() {
        outputStream.print(currentPath.getName() + File.separator);
        if (currentTable != null) {
            outputStream.print(currentTable.getName());
        }
        outputStream.print("$ ");
    }
}
