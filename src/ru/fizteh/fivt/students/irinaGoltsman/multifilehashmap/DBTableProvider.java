package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.tools.ColumnTypes;
import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.irinaGoltsman.shell.Code;
import ru.fizteh.fivt.students.irinaGoltsman.shell.MapOfCommands;
import ru.fizteh.fivt.students.irinaGoltsman.shell.ShellCommands;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBTableProvider implements TableProvider {
    private Map<String, Table> allTables = new HashMap<String, Table>();
    private File rootDirectoryOfTables;
    private static final String TABLE_NAME_FORMAT = "[A-Za-zА-Яа-я0-9@.]+";

    public DBTableProvider(File rootDirectory) throws IOException {
        if (!rootDirectory.exists()) {
            if (!rootDirectory.mkdir()) {
                throw new IOException(rootDirectory.getName() + ": not exist and can't be created");
            }
        }
        if (!rootDirectory.isDirectory()) {
            throw new IllegalArgumentException(rootDirectory.getName() + ": not a directory");
        }
        rootDirectoryOfTables = rootDirectory;
        for (File tableFile : rootDirectoryOfTables.listFiles()) {
            Table table = new DBTable(tableFile, this);
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
        if (!tableName.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("get table: error table name");
        }
        return allTables.get(tableName);
    }

    @Override
    public Table createTable(String tableName, List<Class<?>> columnTypes) throws IOException {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("create table: null table name or table name is empty");
        }
        if (!tableName.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("create table: wrong table name");
        }
        if (columnTypes == null || columnTypes.size() == 0) {
            throw new IllegalArgumentException("create table: null column types list");
        }
        ColumnTypes ct = new ColumnTypes();
        ct.checkTypes(columnTypes);
        Table newTable = null;
        File tableFile = new File(rootDirectoryOfTables, tableName);
        if (tableFile.exists()) {
            return null;
        }
        if (!tableFile.mkdir()) {
            throw new IOException("table" + tableName + "can't be create");
        }
        List<String> types = ct.convertListOfClassesToListOfStrings(columnTypes);
        FileManager.writeSignature(tableFile, types);
        newTable = new DBTable(tableFile, this);
        allTables.put(tableName, newTable);
        return newTable;
    }

    @Override
    public void removeTable(String tableName) throws IOException {
        if (tableName == null || !tableName.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("remove table: incorrect table name");
        }
        if (tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("remove table: table name is empty");
        }
        MapOfCommands cm = new MapOfCommands();
        cm.addCommand(new ShellCommands.Remove());
        cm.addCommand(new ShellCommands.ChangeDirectory());
        if (!allTables.containsKey(tableName)) {
            throw new IllegalStateException(String.format("%s not exists", tableName));
        }
        //File table = new File(rootDirectoryOfTables, tableName);
        cm.commandProcessing("cd " + rootDirectoryOfTables.toString());
        Code returnCode = cm.commandProcessing("rm " + tableName);
        if (returnCode != Code.OK) {
            throw new IOException("");
        }
        allTables.remove(tableName);
    }

    @Override
    public String serialize(Table table, Storeable rowOfTable) throws ColumnFormatException {
        Object[] values = new Object[table.getColumnsCount()];
        for (int i = 0; i < table.getColumnsCount(); i++) {
            values[i] = rowOfTable.getColumnAt(i);
            if (values[i] != null && values[i].getClass() != table.getColumnType(i)) {
                throw new ColumnFormatException("storable serialize: expected "
                        + table.getColumnType(i).toString() + " but there is "
                        + values[i].getClass().toString());
            }
        }
        JSONArray array = new JSONArray(values);
        return array.toString();
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (table == null || value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("deserialize: table or value is null, or value is empty");
        }
        JSONArray values;
        try {
            values = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException("deserialize: wrong string: " + e.getMessage(), 0);
        }
        ColumnTypes ct = new ColumnTypes();
        List<Object> parsedValues = ct.parseJSONArray(values, table);
        Storeable parsedRow;
        try {
            parsedRow = createFor(table, parsedValues);
        } catch (ColumnFormatException e) {
            throw new ParseException("deserialize: wrong string: " + e.getMessage(), 0);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("deserialize: wrong string: wrong types count", 0);
        }
        return parsedRow;
    }

    @Override
    public Storeable createFor(Table table) {
        List<Class<?>> types = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(table.getColumnType(i));
        }
        return new DBStoreable(types);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Class<?>> types = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(table.getColumnType(i));
        }
        Storeable row = new DBStoreable(types);
        for (int i = 0; i < values.size(); i++) {
            row.setColumnAt(i, values.get(i));
        }
        return row;
    }
}
