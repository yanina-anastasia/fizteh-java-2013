package ru.fizteh.fivt.students.eltyshev.storable.database;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseRow implements Storeable {
    List<Class<?>> classes = new ArrayList<>();
    List<Object> columns = new ArrayList<>();

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, value.getClass());
        columns.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkBounds(columnIndex);

        return columns.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Integer.class);

        return (Integer) columns.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Long.class);

        return (Long) columns.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Byte.class);

        return (Byte) columns.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Float.class);

        return (Float) columns.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Double.class);

        return (Double) columns.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Boolean.class);

        return (Boolean) columns.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, String.class);

        return (String) columns.get(columnIndex);
    }

    public void addColumn(Class<?> columnType) {
        classes.add(columnType);
    }

    public void setColumns(List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values.size() != classes.size()) {
            throw new IndexOutOfBoundsException();
        }

        columns.clear();

        for (int index = 0; index < values.size(); ++index) {
            columns.add(values.get(index));
        }
    }

    private void checkBounds(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= classes.size()) {
            throw new IndexOutOfBoundsException(String.format("index out of bound: %d", index));
        }
    }

    private void checkColumnType(int columnIndex, Class<?> type) throws ColumnFormatException {
        if (classes.get(columnIndex) != type) {
            throw new ColumnFormatException();
        }
    }

    @Override
    public String toString() {
        return StoreableUtils.join(columns);
    }
}
