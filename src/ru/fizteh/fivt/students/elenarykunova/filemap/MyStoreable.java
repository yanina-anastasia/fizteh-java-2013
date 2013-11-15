package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class MyStoreable implements Storeable {

    private final List<Class<?>> myTypes = new ArrayList<Class<?>>();
    private List<Object> myValues = new ArrayList<Object>();

    public MyStoreable(Table table) {
        for (int i = 0; i < table.getColumnsCount(); i++) {
            myTypes.add(table.getColumnType(i));
            myValues.add(null);
        }
    }

    public MyStoreable(Table table, List<?> values)
            throws IndexOutOfBoundsException, ColumnFormatException {
        if (values == null || values.size() != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException(
                    "number of columns and size of values list mismatch");
        }
        for (int i = 0; i < table.getColumnsCount(); i++) {
            if (values.get(i) != null
                    && !table.getColumnType(i).equals(values.get(i).getClass())) {
                throw new ColumnFormatException("createFor: types mismatch");
            }
        }
        for (int i = 0; i < table.getColumnsCount(); i++) {
            myTypes.add(table.getColumnType(i));
            myValues.add(values.get(i));
        }
    }

    private boolean isCorrectIndex(int columnIndex) {
        return (columnIndex >= 0 && columnIndex < myTypes.size());
    }

    @Override
    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (!isCorrectIndex(columnIndex)) {
            throw new IndexOutOfBoundsException("incorrect index");
        }
        if (value != null) {
            if (!value.getClass().equals(myTypes.get(columnIndex))) {
                throw new ColumnFormatException(
                        "type of value mismatches type of column : expected "
                                + myTypes.get(columnIndex) + " but was "
                                + value.getClass());
            }
        } else {
            throw new ColumnFormatException("setColumnAt: empty type");
        }
        myValues.add(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (!isCorrectIndex(columnIndex)) {
            throw new IndexOutOfBoundsException("incorrect index");
        }
        return myValues.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        if (!isCorrectIndex(columnIndex)) {
            throw new IndexOutOfBoundsException("incorrect index");
        }
        if (!myTypes.get(columnIndex).equals(Integer.class)) {
            throw new ColumnFormatException(
                    "type of value mismatches type of column");
        }
        return (Integer) myValues.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        if (!isCorrectIndex(columnIndex)) {
            throw new IndexOutOfBoundsException("incorrect index");
        }
        if (!myTypes.get(columnIndex).equals(Long.class)) {
            throw new ColumnFormatException(
                    "type of value mismatches type of column");
        }
        return (Long) myValues.get(columnIndex);

    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        if (!isCorrectIndex(columnIndex)) {
            throw new IndexOutOfBoundsException("incorrect index");
        }
        if (!myTypes.get(columnIndex).equals(Byte.class)) {
            throw new ColumnFormatException(
                    "type of value mismatches type of column");
        }
        return (Byte) myValues.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        if (!isCorrectIndex(columnIndex)) {
            throw new IndexOutOfBoundsException("incorrect index");
        }
        if (!myTypes.get(columnIndex).equals(Float.class)) {
            throw new ColumnFormatException(
                    "type of value mismatches type of column");
        }
        return (Float) myValues.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        if (!isCorrectIndex(columnIndex)) {
            throw new IndexOutOfBoundsException("incorrect index");
        }
        if (!myTypes.get(columnIndex).equals(Double.class)) {
            throw new ColumnFormatException(
                    "type of value mismatches type of column");
        }
        return (Double) myValues.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        if (!isCorrectIndex(columnIndex)) {
            throw new IndexOutOfBoundsException("incorrect index");
        }
        if (!myTypes.get(columnIndex).equals(Boolean.class)) {
            throw new ColumnFormatException(
                    "type of value mismatches type of column");
        }
        return (Boolean) myValues.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException,
            IndexOutOfBoundsException {
        if (!isCorrectIndex(columnIndex)) {
            throw new IndexOutOfBoundsException("incorrect index");
        }
        if (!myTypes.get(columnIndex).equals(String.class)) {
            throw new ColumnFormatException(
                    "type of value mismatches type of column");
        }
        return (String) myValues.get(columnIndex);
    }
}
