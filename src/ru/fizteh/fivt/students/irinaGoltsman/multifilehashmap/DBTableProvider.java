package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.irinaGoltsman.shell.Code;
import ru.fizteh.fivt.students.irinaGoltsman.shell.MapOfCommands;
import ru.fizteh.fivt.students.irinaGoltsman.shell.ShellCommands;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DBTableProvider implements TableProvider {
    private Map<String, Table> allTables = new HashMap<String, Table>();
    private File rootDirectoryOfTables;
    public DBTableProvider(File rootDirectory) throws IOException {
        if (!rootDirectory.exists()) {
            if (!rootDirectory.mkdir()) {
                throw new IOException(rootDirectory.getName() + ": not exist and can't be created");
            }
        }
        if (!rootDirectory.isDirectory()) {
            throw new IOException(rootDirectory.getName() + ": not a directory");
        }
        rootDirectoryOfTables = rootDirectory;
        for (File tableFile : rootDirectoryOfTables.listFiles()) {
            Table table = new DBTable(tableFile);
            allTables.put(tableFile.getName(), table);
        }
    }

    @Override
    public Table getTable(String tableName) throws IllegalArgumentException {
        if (tableName == null) {
            throw new IllegalArgumentException("null table name");
        }
        if (tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("table name is empty");
        }
        if (tableName.contains("\\")) {
            throw new RuntimeException("table name: can not be null");
        }
        return allTables.get(tableName);
    }

    @Override
    public Table createTable(String tableName) throws IllegalArgumentException {
        if (tableName == null) {
            throw new IllegalArgumentException("table name: can not be null");
        }
        if (tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("table name is empty");
        }
        if (tableName.contains("\\")) {
            throw new RuntimeException("table name: can not be null");
        }
        File tableFile = new File(rootDirectoryOfTables, tableName);
        if (tableFile.exists()) {
            return null;
        }
        if (!tableFile.mkdir()) {
            return null;
        }
        try {
            Table newTable = new DBTable(tableFile);
            allTables.put(tableName, newTable);
            return newTable;
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            return null;
        }
    }

    @Override
    public void removeTable(String tableName) throws IllegalArgumentException, IllegalStateException {
        if (tableName == null || tableName.contains("\\")) {
            throw new IllegalArgumentException("incorrect table name");
        }
        if (tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("table name is empty");
        }
        if (!allTables.containsKey(tableName)) {
            throw new IllegalStateException("no such table");
        }
        //File table = new File(rootDirectoryOfTables, tableName);
        MapOfCommands cm = new MapOfCommands();
        cm.addCommand(new ShellCommands.Remove());
        cm.addCommand(new ShellCommands.ChangeDirectory());
        cm.commandProcessing("cd " + rootDirectoryOfTables.toString());
        Code returnCode = cm.commandProcessing("rm " + tableName);
        if (returnCode != Code.OK) {
            throw new RuntimeException("");
        }
        allTables.remove(tableName);
    }
}
