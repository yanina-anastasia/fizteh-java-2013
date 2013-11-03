package ru.fizteh.fivt.students.irinapodorozhnaya.storable;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class MyStoreable implements Storeable {

    List<Object> values;
    Table table;
    
    public MyStoreable(Table table) {
        this.table = table;
        values = new ArrayList<>(table.getColumnsCount());
    }
    
    @Override
    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {
        
        if (!value.getClass().equals(table.getColumnType(columnIndex))) {
            throw new ColumnFormatException();
        }
        values.add(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        
        Object value = values.get(columnIndex);
        if (!value.getClass().equals(Integer.class)) {
            throw new ColumnFormatException();
        }
        return (Integer) value;
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (!value.getClass().equals(Long.class)) {
            throw new ColumnFormatException();
        }
        return (Long) value;
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (!value.getClass().equals(Byte.class)) {
            throw new ColumnFormatException();
        }
        return (Byte) value;
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (!value.getClass().equals(Float.class)) {
            throw new ColumnFormatException();
        }
        return (Float) value;
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (!value.getClass().equals(Double.class)) {
            throw new ColumnFormatException();
        }
        return (Double) value;
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (!value.getClass().equals(Boolean.class)) {
            throw new ColumnFormatException();
        }
        return (Boolean) value;
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (!value.getClass().equals(String.class)) {
            throw new ColumnFormatException();
        }
        return (String) value;
    }
}
