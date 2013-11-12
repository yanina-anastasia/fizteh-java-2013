package ru.fizteh.fivt.students.irinapodorozhnaya.storeable;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.XMLSerializer;

import javax.xml.stream.XMLStreamException;

public class MyStoreable implements Storeable{

    private final List<Object> values;    
    private final Table table;

    public MyStoreable(Table table) {
        this.table = table;
        values = new ArrayList<>(table.getColumnsCount());
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            values.add(null);
        }
    }

    @Override
    public String toString() {
        try {
            return XMLSerializer.serialize(table, this);
        } catch (XMLStreamException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || !Storeable.class.isInstance(o)) {
            return false;
        }
        Storeable st = (Storeable) o;
        int i = 0;
        for (Object object: values) {
            if (object == null) {
                if (st.getColumnAt(i++) != null) {
                    return false;
                }
            } else if (st.getColumnAt(i) == null ) {
                return false;
            } else if (!object.equals(st.getColumnAt(i++))) {
                return false;
            }
        }

        try {
            st.getColumnAt(i);
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
        return false;
    }

    @Override
    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {

        if (value != null && !value.getClass().equals(table.getColumnType(columnIndex))) {
            throw new ColumnFormatException(columnIndex + " column has incorrect format");
        }
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (values.isEmpty()) {
            return null;
        }
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        
        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Integer.class)) {
            throw new ColumnFormatException();
        }
        return (Integer) value;
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Long.class)) {
            throw new ColumnFormatException();
        }
        return (Long) value;
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Byte.class)) {
            throw new ColumnFormatException();
        }
        return (Byte) value;
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Float.class)) {
            throw new ColumnFormatException();
        }
        return (Float) value;
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Double.class)) {
            throw new ColumnFormatException();
        }
        return (Double) value;
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(Boolean.class)) {
            throw new ColumnFormatException();
        }
        return (Boolean) value;
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {

        Object value = values.get(columnIndex);
        if (value != null && !value.getClass().equals(String.class)) {
            throw new ColumnFormatException();
        }
        return (String) value;
    }
}
