package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;


import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class StoreableDataValue implements Storeable {

    private List<Object> columnValues = new ArrayList<Object>();
    private List<Class<?>> columnTypes = new ArrayList<Class<?>>();

    public StoreableDataValue(List<Class<?>> types) {
        columnTypes = types;
    }

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexValidity(columnIndex);
        if (value != null) {
            checkClassEquivalence(columnIndex, value.getClass());
        }
        columnValues.set(columnIndex, value);
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkColumnIndexValidity(columnIndex);
        return columnValues.get(columnIndex);
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexValidity(columnIndex);
        checkClassEquivalence(columnIndex, Integer.class);
        return (Integer) columnValues.get(columnIndex);
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexValidity(columnIndex);
        checkClassEquivalence(columnIndex, Integer.class);
        return (Long) columnValues.get(columnIndex);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexValidity(columnIndex);
        checkClassEquivalence(columnIndex, Integer.class);
        return (Byte) columnValues.get(columnIndex);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexValidity(columnIndex);
        checkClassEquivalence(columnIndex, Integer.class);
        return (Float) columnValues.get(columnIndex);
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexValidity(columnIndex);
        checkClassEquivalence(columnIndex, Integer.class);
        return (Double) columnValues.get(columnIndex);
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexValidity(columnIndex);
        checkClassEquivalence(columnIndex, Integer.class);
        return (Boolean) columnValues.get(columnIndex);
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexValidity(columnIndex);
        checkClassEquivalence(columnIndex, Integer.class);
        return (String) columnValues.get(columnIndex);
    }

    public void checkColumnIndexValidity(int columnIndex) {
        if (columnIndex >= columnTypes.size() || columnIndex < 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    public void checkClassEquivalence(int columnIndex, Class<?> cls) {
        if (!cls.isAssignableFrom(columnTypes.get(columnIndex))) {
            throw new ColumnFormatException("Invalid column type. It should be " + cls.getName());
        }
    }
}
