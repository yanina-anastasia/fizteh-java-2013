package ru.fizteh.fivt.students.baldindima.junit;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.json.JSONArray;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class DataBaseTable implements TableProvider {

    private String tableDirectory;
    private Map<String, DataBase> tables;

    public DataBaseTable(String nTableDirectory) {
        tableDirectory = nTableDirectory;
        tables = new HashMap();
    }


    private void checkName(String name) {
        if ((name == null) || name.trim().length() == 0) {
            throw new IllegalArgumentException("Cannot create table");
        }

        if (name.matches("[" + '"' + "'\\/:/*/?/</>/|/.\\\\]+") || name.contains(File.separator)
                || name.contains(".")) {
            throw new RuntimeException("Wrong symbols");
        }
    }

    public Table createTable(String name, List<Class<?>> types) throws IOException {
        checkName(name);
        String path = tableDirectory + File.separator + name;

        File file = new File(path);

        if (file.exists()) {
            return null;
        }

        if (!file.mkdir()) {
            throw new RuntimeException("Cannot create table " + name);
        }

        if (types == null || types.size() == 0) {
            throw new IllegalArgumentException("wrong list of types");
        }

        DataBase table = new DataBase(path, this, types);

        tables.put(name, table);
        return table;


    }

    public Table getTable(String name) {
        checkName(name);
        String path = tableDirectory + File.separator + name;

        File file = new File(path);
        if ((!file.exists()) || (file.isFile())) {
            return null;
        }

        if (tables.containsKey(name)) {
            return tables.get(name);
        } else {

            try {
                DataBase table = new DataBase(path, this);
                tables.put(name, table);
                return table;
            } catch (IOException e) {
                throw new RuntimeException();
            }

        }
    }

    public void removeTable(String name) throws IOException {
        checkName(name);
        String path = tableDirectory + File.separator + name;

        File file = new File(path);

        if (!file.exists()) {
            throw new IllegalStateException("Table not exist");
        }

        if (tables.containsKey(name)) {
            tables.get(name).drop();
            tables.remove(name);

        } else {
            DataBase base = new DataBase(name, this);
            base.drop();

        }


        if (!file.delete()) {
            throw new RuntimeException("Cannot delete a table " + name);
        }

    }


    @Override
    public Storeable deserialize(Table table, String value)
            throws ParseException {
        JSONArray jsonValue = new JSONArray(value);
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < jsonValue.length(); ++i) {
            values.add(jsonValue.get(i));
        }

        Storeable storeable;
        try {
            storeable = createFor(table, values);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalud number of arguments", 0);
        } catch (ColumnFormatException e) {
            throw new ParseException(e.getMessage(), 0);
        }

        return storeable;
    }


    public String serialize(Table table, Storeable value)
            throws ColumnFormatException {
        return JSONClass.serialize(table, value);
    }


    public Storeable createFor(Table table) {
        return new BaseStoreable(table);
    }


    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        BaseStoreable storeable = new BaseStoreable(table);
        storeable.setValues(values);
        return storeable;
    }


}
