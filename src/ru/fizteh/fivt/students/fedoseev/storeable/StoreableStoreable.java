package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class StoreableStoreable implements Storeable {
    ArrayList<Class<?>> columnTypes;
    ArrayList<Object> columns;

    public StoreableStoreable(ArrayList<Class<?>> columnTypes) {
        this.columnTypes = columnTypes;

        columns = new ArrayList<>();

        for (Class<?> ignored : this.columnTypes) {
            columns.add(null);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        if (value != null && !value.getClass().equals(columnTypes.get(columnIndex))) {
            throw new ColumnFormatException("wrong type (SET ERROR: invalid type)");
        }

        columns.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);

        return columns.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnType(columnIndex, Integer.class);

        return (Integer) columns.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnType(columnIndex, Long.class);

        return (Long) columns.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnType(columnIndex, Byte.class);

        return (Byte) columns.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnType(columnIndex, Float.class);

        return (Float) columns.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnType(columnIndex, Double.class);

        return (Double) columns.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnType(columnIndex, Boolean.class);

        return (Boolean) columns.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        checkColumnType(columnIndex, String.class);

        return (String) columns.get(columnIndex);
    }

    private void checkColumnIndex(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException("SET | GET ERROR: invalid column index");
        }
    }

    private void checkColumnType(int columnIndex, Object value) throws ColumnFormatException {
        if (!value.getClass().isAssignableFrom(columnTypes.get(columnIndex))) {
            throw new ColumnFormatException(String.format(
                    "wrong type (GET ERROR: incorrect type: expected %s instead of %s)",
                    columnTypes.get(columnIndex).getName(), value.getClass().getName())
            );
        }
    }

    public void setColumns(List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values.size() != columnTypes.size()) {
            throw new IndexOutOfBoundsException();
        }

        columns.clear();

        for (Object value : values) {
            columns.add(value);
        }
    }
}
