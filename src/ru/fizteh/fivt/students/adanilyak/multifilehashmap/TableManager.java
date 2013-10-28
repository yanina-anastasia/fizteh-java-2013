package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 13:27
 */
public class TableManager implements TableProvider {
    private Map<String, Table> allTablesMap = new HashMap<String, Table>();
    private File allTablesDirectory;

    public TableManager(File atDirectory) throws IOException {
        if (!atDirectory.exists()) {
            throw new IOException(atDirectory.getName() + ": not exist");
        }
        if (!atDirectory.isDirectory()) {
            throw new IOException(atDirectory.getName() + ": not a directory");
        }

        allTablesDirectory = atDirectory;
        for (File tableFile : allTablesDirectory.listFiles()) {
            Table table = new TableStorage(tableFile);
            allTablesMap.put(tableFile.getName(), table);
        }
    }

    @Override
    public Table getTable(String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("table name: can not be null");
        }
        return allTablesMap.get(tableName);
    }

    @Override
    public Table createTable(String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("table name: can not be null");
        }

        File tableFile = new File(allTablesDirectory, tableName);
        if (!tableFile.mkdir()) {
            return null;
        }

        try {
            Table newTable = new TableStorage(tableFile);
            allTablesMap.put(tableName, newTable);
            return newTable;
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
            return null;
        }
    }

    @Override
    public void removeTable(String tableName) {
        File tableFile = new File(allTablesDirectory, tableName);
        try {
            DeleteDirectory.rm(tableFile);
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
        }
        allTablesMap.remove(tableName);
    }
}
