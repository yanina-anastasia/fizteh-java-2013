package ru.fizteh.fivt.students.anastasyev.filemap;

//import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.ArrayList;
import java.util.List;

public class MyStoreable implements Storeable {
    private ArrayList<Object> row;
    private ArrayList<Class<?>> columnTypes;

    private void indexCheck(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException(columnIndex + " outOfBounds");
        }
    }

    private void columnFormatCheck(int columnIndex, Class<?> type) {
        if (!type.equals(columnTypes.get(columnIndex))) {
            throw new ColumnFormatException("wrong column format");
        }
    }

    private Object getAt(int columnIndex, Class<?> type) throws ColumnFormatException, IndexOutOfBoundsException {
        indexCheck(columnIndex);
        columnFormatCheck(columnIndex, type);
        return row.get(columnIndex);
    }

    public int getColumnCount() {
        return columnTypes.size();
    }

    public MyStoreable(Table currTable) {
        row = new ArrayList<Object>(currTable.getColumnsCount());
        columnTypes = new ArrayList<Class<?>>(currTable.getColumnsCount());
        for (int i = 0; i < currTable.getColumnsCount(); ++i) {
            columnTypes.add(i, currTable.getColumnType(i));
            row.add(i, null);
        }
    }

    public MyStoreable(Table currTable, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (currTable.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException("The columns count is not equal giving values count");
        }
        row = new ArrayList<Object>(currTable.getColumnsCount());
        columnTypes = new ArrayList<Class<?>>(currTable.getColumnsCount());
        for (int i = 0; i < currTable.getColumnsCount(); ++i) {
            columnTypes.add(i, currTable.getColumnType(i));
            if (values.get(i).equals(null)) {
                row.add(i, null);
            } else {
                /*if (!currTable.getColumnType(i).equals(values.get(i).getClass())) {
                    throw new ColumnFormatException("Column type is not equal giving value type");
                }*/
                row.add(i, values.get(i));
            }
        }
    }

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        indexCheck(columnIndex);
        if (value != null) {
            columnFormatCheck(columnIndex, value.getClass());
        }
        row.set(columnIndex, value);
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        indexCheck(columnIndex);
        return row.get(columnIndex);
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Integer) getAt(columnIndex, Integer.class);
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Long) getAt(columnIndex, Long.class);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Byte) getAt(columnIndex, Byte.class);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Float) getAt(columnIndex, Float.class);
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Double) getAt(columnIndex, Double.class);
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Boolean) getAt(columnIndex, Boolean.class);
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (String) getAt(columnIndex, String.class);
    }
}
