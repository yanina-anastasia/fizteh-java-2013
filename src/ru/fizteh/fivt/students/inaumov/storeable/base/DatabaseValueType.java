package ru.fizteh.fivt.students.inaumov.storeable.base;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.inaumov.storeable.StoreableUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseValueType implements Storeable {
    final List<Class<?>> classes = new ArrayList<Class<?>>();
    List<Object> columns = new ArrayList<Object>();

    public DatabaseValueType(List<Class<?>> classes) {
        this.classes.addAll(classes);

        for (int i = 0; i < classes.size(); ++i) {
            columns.add(null);
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        if (value != null) {
            checkColumnType(columnIndex, value);
            try {
                StoreableUtils.isValueCorrect(value, value.getClass());
            } catch (ParseException e) {
                throw new IllegalArgumentException("incorrect value");
            }
        }

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

    private void checkBounds(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= classes.size()) {
            throw new IndexOutOfBoundsException("index " + index + " is out of bounds");
        }
    }

    private void checkColumnType(int columnIndex, Object value) throws ColumnFormatException {
        if (!value.getClass().isAssignableFrom(classes.get(columnIndex))) {
            throw new ColumnFormatException("incorrect type: expected" + classes.get(columnIndex).getName() + ", got " +
                        value.getClass().getName());
        }
    }
}
