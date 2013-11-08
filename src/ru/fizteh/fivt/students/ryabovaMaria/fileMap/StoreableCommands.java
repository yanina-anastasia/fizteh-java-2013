package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.util.ArrayList;
import java.util.List;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class StoreableCommands implements Storeable{
    private ArrayList<Object> values;
    private ArrayList<Class<?>> types;
    
    StoreableCommands(List<Class<?>> types) {
        this.types = new ArrayList(types);
        values = new ArrayList(types.size());
        Object empty = new Object();
        for (int i = 0; i < types.size(); ++i) {
            values.add(empty);
        }
    }
    
    StoreableCommands(List<Object> values, List<Class<?>> types) throws IndexOutOfBoundsException, ColumnFormatException {
        if (values.size() != types.size()) {
            throw new IndexOutOfBoundsException("incorrect number of elements");
        }
        for (int i = 0; i < types.size(); ++i) {
            Class type = types.get(i);
            Class value = values.get(i).getClass();
            if (!type.getSimpleName().equals(value.getSimpleName())) {
                throw new ColumnFormatException("wrong type");
            }
        }
        this.values = new ArrayList(values);
        this.types = new ArrayList(types);
    }
    
    private void isCorrectIndex(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= values.size()) {
            throw new IndexOutOfBoundsException();
        }
    }
    
    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        isCorrectIndex(columnIndex);
        if (value == null) {
            values.add(columnIndex, null);
            return;
        }
        Class type = types.get(columnIndex);
        Class valueClass = value.getClass();
        if (!type.getSimpleName().equals(valueClass.getSimpleName())) {
            throw new ColumnFormatException();
        } else {
            values.add(columnIndex, value);
        }
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        isCorrectIndex(columnIndex);
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        isCorrectIndex(columnIndex);
        try {
            Integer result = (Integer) getColumnAt(columnIndex);
            return result;
        } catch (Exception e) {
            throw new ColumnFormatException();
        }
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        isCorrectIndex(columnIndex);
        try {
            Long result = (Long) getColumnAt(columnIndex);
            return result;
        } catch (Exception e) {
            throw new ColumnFormatException();
        }
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        isCorrectIndex(columnIndex);
        try {
            Byte result = (Byte) getColumnAt(columnIndex);
            return result;
        } catch (Exception e) {
            throw new ColumnFormatException();
        }
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        isCorrectIndex(columnIndex);
        try {
            Float result = (Float) getColumnAt(columnIndex);
            return result;
        } catch (Exception e) {
            throw new ColumnFormatException();
        }
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        isCorrectIndex(columnIndex);
        try {
            Double result = (Double) getColumnAt(columnIndex);
            return result;
        } catch (Exception e) {
            throw new ColumnFormatException();
        }
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        isCorrectIndex(columnIndex);
        try {
            Boolean result = (Boolean) getColumnAt(columnIndex);
            return result;
        } catch (Exception e) {
            throw new ColumnFormatException();
        }
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        isCorrectIndex(columnIndex);
        try {
            String result = (String) getColumnAt(columnIndex);
            return result;
        } catch (Exception e) {
            throw new ColumnFormatException();
        }
    }
}
