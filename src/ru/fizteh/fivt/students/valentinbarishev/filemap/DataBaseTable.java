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

        DataBase table = new DataBase(fullPath, this, columnTypes);
        tableInUse.put(tableName, table);
        return table;
    }

    @Override
    public void removeTable(final String tableName) throws IOException {
        checkName(tableName);
        String fullPath = tableDir + File.separator + tableName;

        File file = new File(fullPath);
        if (!file.exists()) {
            throw new IllegalStateException("Table not exist already!");
        }

        if (tableInUse.containsKey(tableName)) {
            DataBase base = tableInUse.get(tableName);
            base.drop();
            if (!file.delete()) {
                throw new DataBaseException("Cannot delete a file " + tableName);
            }
            tableInUse.remove(tableName);
        }
    }

    @Override
    public Table getTable(String tableName) {
        checkName(tableName);
        String fullPath = tableDir + File.separator + tableName;

        File file = new File(fullPath);
        if ((!file.exists()) || (file.isFile())) {
            return null;
        }
        if (tableInUse.containsKey(tableName)) {
            return tableInUse.get(tableName);
        } else {
            try {
                DataBase table = new DataBase(fullPath, this, null);
                tableInUse.put(tableName, table);
                return table;
            } catch (IOException e) {
                throw new DataBaseException("Wrong format database " + fullPath);
            }
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return WorkWithJSON.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return WorkWithJSON.serialize(table, value);
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

