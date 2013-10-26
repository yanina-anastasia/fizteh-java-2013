package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.DataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.CommandUtils;


import java.io.File;
import java.io.IOException;

public class MultiFileHashMapProvider implements TableProvider {
    private File curTable = null;
    private File nextTable;
    private File workingDirectory;
    public DataTable dataStorage;

    public MultiFileHashMapProvider(File dir) {
        workingDirectory = dir;
    }

    public void setCurTable(File newTable) {
        if (nextTable == null) {
            nextTable = curTable;
        }
        curTable = newTable;
        dataStorage = new DataTable(curTable.getName());
    }

    public File getCurTable() {
        return curTable;
    }

    public void setNextTable(File newTable) {
        nextTable = newTable;
    }

    public File getNextTable() {
        return nextTable;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        File tableDir = new File(workingDirectory, name);
        try {
            tableDir = tableDir.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Programme's mistake in getting canonical file");
        }
        if (!tableDir.exists()) {
            return null;
        } else {
            if (!tableDir.isDirectory()) {
                throw new IllegalArgumentException("The table should be a directory");
            }
            return new DataTable(name);
        }
    }

    public Table createTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        File newTable = new File(workingDirectory, name);
        try {
            newTable = newTable.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Programme's mistake in getting canonical file");
        }
        if (newTable.exists()) {
            if (!newTable.isDirectory()) {
                throw new IllegalArgumentException(name + " should be a directory");
            }
            return null;
        } else {
            newTable.mkdir();
            return new DataTable(name);
        }
    }

    public void removeTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        File table = new File(workingDirectory, name);
        try {
            table = table.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Programme's mistake in getting canonical file");
        }
        if (!table.exists()) {
            throw new IllegalStateException(name + " not exist");
        }
        if (!table.isDirectory()) {
            throw new IllegalArgumentException("table " + name + " should be a directory");
        }
        try {
            CommandUtils.recDeletion(table);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}


