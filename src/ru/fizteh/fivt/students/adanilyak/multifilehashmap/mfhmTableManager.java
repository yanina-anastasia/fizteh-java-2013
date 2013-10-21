package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.adanilyak.tools.tlDeleteDirectory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 13:27
 */
public class mfhmTableManager implements TableProvider {
    private Map<String, Table> allTablesMap = new HashMap<String, Table>();
    private File allTablesDirectory;

    public mfhmTableManager(File atDirectory) throws Exception {
        if (!atDirectory.exists()) {
            throw new Exception(atDirectory.getName() + ": not exist");
        }
        if (!atDirectory.isDirectory()) {
            throw new Exception(atDirectory.getName() + ": not a directory");
        }

        allTablesDirectory = atDirectory;
        for (File tableFile : allTablesDirectory.listFiles()) {
            Table table = new mfhmTableStorage(tableFile);
            allTablesMap.put(tableFile.getName(), table);
        }
    }

    @Override
    public Table getTable(String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("table name: can not be null");
        }

        if (!allTablesMap.containsKey(tableName)) {
            return null;
        }

        File tableFile = new File(allTablesDirectory, tableName);
        try {
            return new mfhmTableStorage(tableFile);
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            return null;
        }
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
            Table newTable = new mfhmTableStorage(tableFile);
            allTablesMap.put(tableName, newTable);
            return newTable;
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            return null;
        }
    }

    @Override
    public void removeTable(String tableName) {
        File tableFile = new File(allTablesDirectory, tableName);
        try {
            tlDeleteDirectory.rm(tableFile);
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
        }
        allTablesMap.remove(tableName);
    }
}
