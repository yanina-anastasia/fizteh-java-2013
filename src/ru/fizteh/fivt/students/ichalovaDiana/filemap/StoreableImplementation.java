package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class StoreableImplementation implements Storeable {
    private List<Object> values;
    private Table table;
    
    StoreableImplementation(Table table) {
        if (table == null) {
            throw new RuntimeException("table is null");
        }
        this.table = table;
        
        this.values = new ArrayList<Object>(); 
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            values.add(null);
        }
    }
    
    StoreableImplementation(Table table, List<?> values) 
            throws ColumnFormatException, IndexOutOfBoundsException { // check values
        if (table == null) {
            throw new RuntimeException("table is null");
        }
        this.table = table;
        
        areValidValues(table, values);
        
        this.values = new ArrayList<Object>();
        for (Object value : values) {
            this.values.add(value);
        }
    }
    
    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value != null && !value.getClass().equals(table.getColumnType(columnIndex))) {
            throw new ColumnFormatException("Error while setting value " + value + " at index " + columnIndex);
        }
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!table.getColumnType(columnIndex).equals(Integer.class)) {
            throw new ColumnFormatException("Error while getting integer value at index " + columnIndex);
        }
        return (Integer) values.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!table.getColumnType(columnIndex).equals(Long.class)) {
            throw new ColumnFormatException("Error while getting long value at index " + columnIndex);
        }
        return (Long) values.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!table.getColumnType(columnIndex).equals(Byte.class)) {
            throw new ColumnFormatException("Error while getting byte value at index " + columnIndex);
        }
        return (Byte) values.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!table.getColumnType(columnIndex).equals(Float.class)) {
            throw new ColumnFormatException("Error while getting float value at index " + columnIndex);
        }
        return (Float) values.get(columnIndex);
    }
    
    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!table.getColumnType(columnIndex).equals(Double.class)) {
            throw new ColumnFormatException("Error while getting double value at index " + columnIndex);
        }
        return (Double) values.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!table.getColumnType(columnIndex).equals(Boolean.class)) {
            throw new ColumnFormatException("Error while getting boolean value at index " + columnIndex);
        }
        return (Boolean) values.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (!table.getColumnType(columnIndex).equals(String.class)) {
            throw new ColumnFormatException("Error while getting string value at index " + columnIndex);
        }
        return (String) values.get(columnIndex);
    }
    
    private static void areValidValues(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (values == null) {
            throw new ColumnFormatException("Empty values");
        }
        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException("Illegal number of values");
        }
        
        for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
            if (values.get(columnIndex) != null 
                    && !values.get(columnIndex).getClass().equals(table.getColumnType(columnIndex))) {
                throw new ColumnFormatException("Type mismatch: given " + values.get(columnIndex).getClass() 
                        + " instead of " + table.getColumnType(columnIndex));
            }
        }
    }
    
}
