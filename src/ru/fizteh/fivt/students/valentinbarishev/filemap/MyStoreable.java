package ru.fizteh.fivt.students.valentinbarishev.filemap;

import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyStoreable implements Storeable{
    private List<Class<?>> types = new ArrayList<>();
    private List<Object> values = new ArrayList<>();

    public MyStoreable(Table table, List<?> newValues) {
        if (newValues == null) {
            throw new IndexOutOfBoundsException("MyStoreable: list of values cannot be null");
        }
        if (newValues.size() != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("MyStoreable: invalid number of values");
        }

        for (int i = 0; i < newValues.size(); ++i) {
            if (newValues.get(i).getClass() != table.getColumnType(i)) {
                throw new ColumnFormatException("MyStoreable: invalid type of value!");
            }
            types.add(table.getColumnType(i));
            values.add(newValues.get(i));
        }
    }

    public MyStoreable(Table table) {
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(table.getColumnType(i));
            values.add(null);
        }
    }


    void checkBounds(int index) {
        if (index < 0 || index >= types.size()) {
            throw new IndexOutOfBoundsException("MyStoreable: index out of bounds!");
        }
    }

    void checkType(int index, Object value) {
        if ((value != null) && (value.getClass() != types.get(index))) {
            throw new ColumnFormatException();
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (value == JSONObject.NULL) {
            value = null;
        }
        checkBounds(columnIndex);
        checkType(columnIndex, value);
        values.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Integer.class);
        return (Integer) values.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Long.class);
        return (long) values.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Byte.class);
        return (byte) values.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Float.class);
        return (float) values.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Double.class);
        return (double) values.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Boolean.class);
        return (boolean) values.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, String.class);
        return (String) values.get(columnIndex);
    }
}
