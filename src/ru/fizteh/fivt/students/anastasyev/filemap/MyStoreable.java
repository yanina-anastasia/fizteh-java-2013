package ru.fizteh.fivt.students.anastasyev.filemap;

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
            Object put = values.get(i);
            if (put.equals(null)) {
                row.add(null);
            } else if (columnTypes.get(i) == Integer.class) {
                if (put.getClass() == Integer.class) {
                    row.add(put);
                } else {
                    throw new ColumnFormatException("Column type is not equal giving value type");
                }
            } else if (columnTypes.get(i) == Long.class) {
                if (put.getClass() == Long.class) {
                    row.add(put);
                } else if (put.getClass() == Integer.class) {
                    row.add(Integer.class.cast(put).longValue());
                } else {
                    throw new ColumnFormatException("Column type is not equal giving value type");
                }
            } else if (columnTypes.get(i) == Byte.class) {
                if (put.getClass() == Byte.class) {
                    row.add(put);
                } else if (put.getClass() == Integer.class) {
                    Integer number = Integer.class.cast(put);
                    if (number > Byte.MAX_VALUE || number < Byte.MIN_VALUE) {
                        throw new ColumnFormatException("Column type is not equal giving value type");
                    }
                    row.add(number.byteValue());
                } else {
                    throw new ColumnFormatException("Column type is not equal giving value type");
                }
            } else if (columnTypes.get(i) == Float.class) {
                if (put.getClass() == Float.class) {
                    row.add(put);
                } else if (put.getClass() == Double.class) {
                    row.add(Double.class.cast(put).floatValue());
                } else if (put.getClass() == Integer.class) {
                    row.add(Integer.class.cast(put).floatValue());
                } else {
                    throw new ColumnFormatException("Column type is not equal giving value type");
                }
            } else if (columnTypes.get(i) == Double.class) {
                if (put.getClass() == Double.class) {
                    row.add(put);
                } else if (put.getClass() == Float.class) {
                    row.add(Float.class.cast(put).doubleValue());
                } else if (put.getClass() == Integer.class) {
                    row.add(Integer.class.cast(put).doubleValue());
                } else {
                    throw new ColumnFormatException("Column type is not equal giving value type");
                }
            } else if (columnTypes.get(i) == Boolean.class) {
                if (put.getClass() == Boolean.class) {
                    row.add(put);
                } else {
                    throw new ColumnFormatException("Column type is not equal giving value type");
                }
            } else if (columnTypes.get(i) == String.class) {
                if (put.getClass() == String.class) {
                    row.add(put);
                } else {
                    throw new ColumnFormatException("Column type is not equal giving value type");
                }
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
        return Integer.class.cast(getAt(columnIndex, Integer.class));
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return Long.class.cast(getAt(columnIndex, Long.class));
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return Byte.class.cast(getAt(columnIndex, Byte.class));
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return Float.class.cast(getAt(columnIndex, Float.class));
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return Double.class.cast(getAt(columnIndex, Double.class));
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return Boolean.class.cast(getAt(columnIndex, Boolean.class));
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return String.class.cast(getAt(columnIndex, String.class));
    }
}
