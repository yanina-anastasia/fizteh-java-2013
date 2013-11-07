package ru.fizteh.fivt.students.mikhaylova_daria.db;

import ru.fizteh.fivt.storage.structured.*;

import java.util.ArrayList;


public class Value implements Storeable {

    private ArrayList<Object> value;
    private Table table;

    Value(Table table) {
        this.table = table;
        value = new ArrayList<>(table.getColumnsCount());
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
        return (Integer) value.get(columnIndex);
    }


    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(Long.class)) {
            throw new ColumnFormatException("Type og this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        return (Long) value.get(columnIndex);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(Byte.class)) {
            throw new ColumnFormatException("Type og this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        return (Byte) value.get(columnIndex);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(Float.class)) {
            throw new ColumnFormatException("Type og this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        return (Float) value.get(columnIndex);
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(Double.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        return (Double) value.get(columnIndex);
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(Boolean.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        return (Boolean) value.get(columnIndex);
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!table.getColumnType(columnIndex).equals(String.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + table.getColumnType(columnIndex).getCanonicalName());
        }
        return (String) value.get(columnIndex);
    }

}
