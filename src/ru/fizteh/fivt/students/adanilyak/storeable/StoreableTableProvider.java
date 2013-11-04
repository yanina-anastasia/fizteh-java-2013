package ru.fizteh.fivt.students.adanilyak.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.adanilyak.serializer.JSONserializer;
import ru.fizteh.fivt.students.adanilyak.tools.CheckOnCorrect;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 16:50
 */
public class StoreableTableProvider implements TableProvider {
    private Map<String, Table> allTablesMap = new HashMap<>();
    private File allTablesDirectory;

    public StoreableTableProvider(File atDirectory) throws IOException {
        if (atDirectory == null) {
            throw new IllegalArgumentException("Directory is not set");
        }

        if (!atDirectory.exists()) {
            atDirectory.mkdir();
        } else if (!atDirectory.isDirectory()) {
            throw new IllegalArgumentException(atDirectory.getName() + ": not a directory");
        }
        allTablesDirectory = atDirectory;

        for (File tableFile : allTablesDirectory.listFiles()) {
            Table table = new StoreableTable(tableFile, this);
            allTablesMap.put(tableFile.getName(), table);
        }
    }

    @Override
    public Table getTable(String tableName) {
        if (!CheckOnCorrect.goodName(tableName)) {
            throw new IllegalArgumentException("get table: name is bad");
        }
        return allTablesMap.get(tableName);
    }

    @Override
    public Table createTable(String tableName, List<Class<?>> columnTypes) throws IOException {
        if (!CheckOnCorrect.goodName(tableName) || !CheckOnCorrect.goodColumnTypes(columnTypes)) {
            throw new IllegalArgumentException("create table: name or column types is bad");
        }

        File tableFile = new File(allTablesDirectory, tableName);
        if (!tableFile.mkdir()) {
            return null;
        }

        Table newTable = new StoreableTable(tableFile, columnTypes, this);
        allTablesMap.put(tableName, newTable);
        return newTable;
    }

    @Override
    public void removeTable(String tableName) throws IOException {
        if (!CheckOnCorrect.goodName(tableName)) {
            throw new IllegalArgumentException("Bad table name");
        }

        if (allTablesMap.get(tableName) == null) {
            throw new IllegalStateException(tableName + " not exists");
        }

        File tableFile = new File(allTablesDirectory, tableName);
        try {
            DeleteDirectory.rm(tableFile);
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
        }
        allTablesMap.remove(tableName);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return JSONserializer.deserialize(table, value, this);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return JSONserializer.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        return new StoreableRow(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return new StoreableRow(table, values);
    }
}
