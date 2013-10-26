package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.irinaGoltsman.shell.Code;
import ru.fizteh.fivt.students.irinaGoltsman.shell.MapOfCommands;
import ru.fizteh.fivt.students.irinaGoltsman.shell.ShellCommands;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DBTableProvider implements TableProvider {
    private Map<String, Table> allTables = new HashMap<String, Table>();
    private File rootDirectoryOfTables;

    public DBTableProvider(File rootDirectory) throws Exception {
        if (!rootDirectory.exists()) {
            throw new Exception(rootDirectory.getName() + ": not exist");
        }
        if (!rootDirectory.isDirectory()) {
            throw new Exception(rootDirectory.getName() + ": not a directory");
        }

        rootDirectoryOfTables = rootDirectory;
        for (File tableFile : rootDirectoryOfTables.listFiles()) {
            Table table = new DBTable(tableFile);
            allTables.put(tableFile.getName(), table);
        }
    }

    @Override
    public Table getTable(String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("table name: can not be null");
        }

        if (!allTables.containsKey(tableName)) {
            return null;
        }

        File tableFile = new File(rootDirectoryOfTables, tableName);
        try {
            return new DBTable(tableFile);
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            return null;
        }
    }

    @Override
    public Table createTable(String tableName) throws IllegalStateException {
        if (tableName == null) {
            throw new IllegalArgumentException("table name: can not be null");
        }
        File tableFile = new File(rootDirectoryOfTables, tableName);
        if (tableFile.exists()) {
            throw new IllegalStateException(tableName + " exists");
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
    public void removeTable(String tableName) throws IllegalStateException {
        File table = new File(rootDirectoryOfTables, tableName);
        MapOfCommands cm = new MapOfCommands();
        cm.addCommand(new ShellCommands.Remove());
        cm.addCommand(new ShellCommands.ChangeDirectory());
        cm.commandProcessing("cd " + rootDirectoryOfTables.toString());
        Code returnCode = cm.commandProcessing("rm " + tableName);
        if (returnCode != Code.OK) {
            throw new IllegalStateException("");
        }
        allTables.remove(tableName);
    }
}
