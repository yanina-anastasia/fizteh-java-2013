package ru.fizteh.fivt.students.mikhaylova_daria.db;

import ru.fizteh.fivt.storage.structured.*;

import java.util.ArrayList;


public class Value implements Storeable {

    private ArrayList<Object> value;
    private Table table;

    Value(Table table) {
        this.table = table;
        value = new ArrayList<>(table.getColumnsCount());
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            value.add(new Object());
        }
    }


    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!(value == null)) {
            if (!value.getClass().equals(table.getColumnType(columnIndex))) {
                throw new ColumnFormatException("Wrong type of value ");
            }
        }
        this.value.add(columnIndex, value);
    }


    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column" + columnIndex);
        }
        return value.get(columnIndex);
    }


    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column" + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(Integer.class)) {
            throw new ColumnFormatException("Type og this column is"
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        Integer integer = null;
        if (value.get(columnIndex) != null) {
            integer = (Integer) value.get(columnIndex);
        }
        return integer;
    }


    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(Long.class)) {
            throw new ColumnFormatException("Type og this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        Long val = null;
        if (value.get(columnIndex) != null) {
            val = (Long) value.get(columnIndex);
        }
        return val;
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(Byte.class)) {
            throw new ColumnFormatException("Type og this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        Byte val = null;
        if (value.get(columnIndex) != null) {
            val = (Byte) value.get(columnIndex);
        }
        return val;
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(Float.class)) {
            throw new ColumnFormatException("Type og this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        Float val = null;
        if (value.get(columnIndex) != null) {
            val = (Float) value.get(columnIndex);
        }
        return val;
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(Double.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        Double val = null;
        if (value.get(columnIndex) != null) {
            val = (Double) value.get(columnIndex);
        }
        return val;
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(Boolean.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        Boolean val = null;
        if (value.get(columnIndex) != null) {
            val = (Boolean) value.get(columnIndex);
        }
        return val;
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(String.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        String val = null;
        if (value.get(columnIndex) != null) {
            val = (String) value.get(columnIndex);
        }
        return val;
    }

}
