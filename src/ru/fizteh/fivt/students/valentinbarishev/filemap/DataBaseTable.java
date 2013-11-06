package ru.fizteh.fivt.students.valentinbarishev.filemap;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public final class DataBaseTable implements TableProvider {
    private String tableDir;
    private Map<String, DataBase> tableInUse;

    public DataBase getTableFromMap(final String name, List<Class<?>> columnTypes) {
        try {
            if (!tableInUse.containsKey(name)) {
                tableInUse.put(name, new DataBase(name, this, columnTypes));
            }
            return tableInUse.get(name);
        } catch (IOException e) {
            throw new DataBaseException("Get table from map failed!");
        }
    }

    public void deleteTableFromMap(final String name) {
       if (tableInUse.containsKey(name)) {
           tableInUse.remove(name);
       }
    }

    public DataBaseTable(String newTableDir) {
        tableDir = newTableDir;
        tableInUse = new HashMap();
    }

    private void checkName(final String name) {
        if ((name == null) || name.trim().length() == 0) {
            throw new IllegalArgumentException("Cannot create table! Wrong name!");
        }

        if (name.matches("[" + '"' + "'\\/:/*/?/</>/|/.\\\\]+") || name.contains(File.separator)
                || name.contains(".")) {
            throw new RuntimeException("Wrong symbols in name!");
        }
    }

    @Override
    public Table createTable(final String tableName, List<Class<?>> columnTypes) throws IOException {
        checkName(tableName);
        String fullPath = tableDir + File.separator + tableName;

        File file = new File(fullPath);

        if (file.exists()) {
            return null;
        }

        if (!file.mkdir()) {
            throw new MultiDataBaseException("Cannot create table " + tableName);
        }

        return getTableFromMap(fullPath, columnTypes);
    }

    @Override
    public void removeTable(final String tableName) throws IOException {
        checkName(tableName);
        String fullPath = tableDir + File.separator + tableName;

        File file = new File(fullPath);
        if (!file.exists()) {
            throw new IllegalStateException("Table not exist already!");
        }

        DataBase base = getTableFromMap(fullPath, null);
        base.drop();
        if (!file.delete()) {
            throw new DataBaseException("Cannot delete a file " + tableName);
        }
        deleteTableFromMap(fullPath);
    }

    @Override
    public Table getTable(String tableName) {
        checkName(tableName);
        String fullPath = tableDir + File.separator + tableName;

        File file = new File(fullPath);
        if ((!file.exists()) || (file.isFile())) {
            return null;
        }
        return getTableFromMap(fullPath, null);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        Storeable result = createFor(table);
        JSONObject input = new JSONObject(value);
        for (Integer i = 0; i < input.length(); ++i) {
            try {
                result.setColumnAt(i, table.getColumnType(i).cast(input.get(i.toString())));
            } catch (ColumnFormatException | IndexOutOfBoundsException e) {
                throw new ParseException("Deserialize exception!", 0);
            }
        }
        return result;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            array.put(value.getColumnAt(i));
        }
        return array.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        return new MyStoreable(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return new MyStoreable(table, values);
    }
}

