package ru.fizteh.fivt.students.kislenko.storeable;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyTableProvider implements TableProvider {
    private HashMap<String, MyTable> tables = new HashMap<String, MyTable>();

    @Override
    public MyTable getTable(String name) {
        if (name == null || !Paths.get(name).getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
            throw new IllegalArgumentException("Incorrect table name.");
        }
        return tables.get(name);
    }

    @Override
    public MyTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (columnTypes == null) {
            throw new IllegalArgumentException("Incorrect column type list.");
        }
        if (columnTypes.size() == 0) {
            throw new IllegalArgumentException("Empty signature.");
        }
        for (Class<?> columnType : columnTypes) {
            if (columnType == null) {
                throw new IllegalArgumentException("Incorrect column types in creating table.");
            }
            if (!columnType.isAssignableFrom(Integer.class) &&
                    !columnType.isAssignableFrom(Long.class) &&
                    !columnType.isAssignableFrom(Byte.class) &&
                    !columnType.isAssignableFrom(Float.class) &&
                    !columnType.isAssignableFrom(Double.class) &&
                    !columnType.isAssignableFrom(Boolean.class) &&
                    !columnType.isAssignableFrom(String.class)) {
                throw new IllegalArgumentException("Incorrect column types in creating table.");
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("Incorrect table name.");
        }
        if (name.contains(".")) {
            throw new RuntimeException("There is a fucking dot!");
        }
        if (!Paths.get(name).getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
            throw new IllegalArgumentException("Incorrect table name.");
        }
        if (tables.containsKey(name)) {
            return null;
        }
        MyTable table = new MyTable(name, columnTypes, this);
        tables.put(name, table);
        return table;
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (name == null || !Paths.get(name).getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
            throw new IllegalArgumentException("Incorrect table name.");
        }
        if (!tables.containsKey(name)) {
            throw new IllegalStateException("Have no table to remove.");
        }
        tables.remove(name);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        JSONArray array = new JSONArray(value);
        if (array.length() != table.getColumnsCount()) {
            throw new ParseException("Incorrect count of columns in input.", Math.min(array.length(), table.getColumnsCount()));
        }
        List<Object> values = new ArrayList<Object>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (array.get(i).getClass().equals(Integer.class) &&
                    table.getColumnType(i).isAssignableFrom(Integer.class)) {
                values.add(array.getInt(i));
            } else if ((array.get(i).getClass().equals(Long.class) ||
                    array.get(i).getClass().equals(Integer.class)) &&
                    table.getColumnType(i).isAssignableFrom(Long.class)) {
                values.add(array.getLong(i));
            } else if (array.get(i).getClass().equals(Integer.class) &&
                    table.getColumnType(i).isAssignableFrom(Byte.class)) {
                Integer a = array.getInt(i);
                values.add(a.byteValue());
            } else if (array.get(i).getClass().equals(Double.class) &&
                    table.getColumnType(i).isAssignableFrom(Float.class)) {
                Double a = array.getDouble(i);
                values.add(a.floatValue());
            } else if (array.get(i).getClass().equals(Double.class) &&
                    table.getColumnType(i).isAssignableFrom(Double.class)) {
                values.add(array.getDouble(i));
            } else if (array.get(i).getClass().equals(Boolean.class) &&
                    table.getColumnType(i).isAssignableFrom(Boolean.class)) {
                values.add(array.getBoolean(i));
            } else if (array.get(i).getClass().equals(String.class) && table.getColumnType(i).isAssignableFrom(String.class)) {
                values.add(array.getString(i));
            } else {
                throw new ParseException("Incorrect value string.", -1);
            }
        }
        return createFor(table, values);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        Object[] values = new Object[table.getColumnsCount()];
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            values[i] = value.getColumnAt(i);
        }
        JSONArray array = new JSONArray(values);
        return array.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        ArrayList<Class<?>> types = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(table.getColumnType(i));
        }
        List<?> values = new ArrayList();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            values.add(null);
        }
        Value v = new Value(types);
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            v.setColumnAt(i, null);
        }
        return v;
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values.size() < table.getColumnsCount()) {
            throw new ColumnFormatException("Invalid list of values.");
        }
        ArrayList<Class<?>> types = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(table.getColumnType(i));
            if (!table.getColumnType(i).equals(values.get(i).getClass())) {
                throw new ColumnFormatException("Invalid list of values.");
            }
        }
        Value v = new Value(types);
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            v.setColumnAt(i, values.get(i));
        }
        return v;
    }
}
