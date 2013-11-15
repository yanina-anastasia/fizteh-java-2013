package ru.fizteh.fivt.students.surakshina.filemap;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class MyStoreable implements Storeable {
    private ArrayList<Object> values = null;
    private ArrayList<Class<?>> types = null;

    public MyStoreable(Table table) {
        this.values = new ArrayList<Object>();
        this.types = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            this.types.add(table.getColumnType(i));
            this.values.add(null);
        }
    }

    @Override
    public int hashCode() {
        return this.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this != null && obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Storeable st = (Storeable) obj;
        for (int i = 0; i < types.size(); i++) {
            if (this.getColumnAt(i) != st.getColumnAt(i)) {
                return false;
            }
        }
        return true;
    }

    public MyStoreable(Table table, List<?> input) {
        int size = input.size();
        if (size != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("wrong type (Incorrect number of values)");
        }
        values = new ArrayList<Object>(table.getColumnsCount());
        types = new ArrayList<Class<?>>(table.getColumnsCount());
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(i, table.getColumnType(i));
            Object key = input.get(i);
            Class<?> currentClass = table.getColumnType(i);
            if (key.equals(null)) {
                values.add(null);
            } else if (currentClass.equals(Integer.class)) {
                if (key.getClass().equals(Integer.class)) {
                    values.add(key);
                } else {
                    throw new ColumnFormatException("wrong type (Incorrect column format)");
                }
            } else if (currentClass.equals(Long.class)) {
                if (key.getClass().equals(Long.class)) {
                    values.add(key);
                } else if (key.getClass().equals(Integer.class)) {
                    values.add(Integer.class.cast(key).longValue());
                } else if (key.getClass().equals(Byte.class)) {
                    values.add(Byte.class.cast(key).longValue());
                } else {
                    throw new ColumnFormatException("wrong type (Incorrect column format)");
                }
            } else if (currentClass.equals(Byte.class)) {
                if (key.getClass().equals(Byte.class)) {
                    values.add(key);
                } else if (key.getClass().equals(Integer.class)) {
                    Integer current = Integer.class.cast(key);
                    if (current > Byte.MAX_VALUE || current < Byte.MIN_VALUE) {
                        throw new ColumnFormatException("wrong type (Incorrect column format)");
                    }
                    values.add(current.byteValue());
                } else if (key.getClass().equals(Long.class)) {
                    Long current = Long.class.cast(key);
                    if (current > Byte.MAX_VALUE || current < Byte.MIN_VALUE) {
                        throw new ColumnFormatException("wrong type (Incorrect column format)");
                    }
                    values.add(current.byteValue());
                } else {
                    throw new ColumnFormatException("wrong type (Incorrect column format)");
                }
            } else if (currentClass.equals(Float.class)) {
                if (key.getClass().equals(Float.class)) {
                    values.add(key);
                } else if (key.getClass().equals(Double.class)) {
                    values.add(Double.class.cast(key).floatValue());
                } else if (key.getClass().equals(Integer.class)) {
                    values.add(Integer.class.cast(key).floatValue());
                } else if (key.getClass().equals(Long.class)) {
                    values.add(Long.class.cast(key).floatValue());
                } else {
                    throw new ColumnFormatException("wrong type (Incorrect column format)");
                }
            } else if (currentClass.equals(Double.class)) {
                if (key.getClass().equals(Double.class)) {
                    values.add(key);
                } else if (key.getClass().equals(Float.class)) {
                    values.add(Float.class.cast(key).doubleValue());
                } else if (key.getClass().equals(Integer.class)) {
                    values.add(Integer.class.cast(key).doubleValue());
                } else {
                    throw new ColumnFormatException("wrong type (Incorrect column format)");
                }
            } else if (currentClass.equals(Boolean.class)) {
                if (key.getClass().equals(Boolean.class)) {
                    values.add(key);
                } else {
                    throw new ColumnFormatException("wrong type (Incorrect column format)");
                }
            } else if (currentClass.equals(String.class)) {
                if (key.getClass().equals(String.class)) {
                    values.add(key);
                } else {
                    throw new ColumnFormatException("wrong type (Incorrect column format)");
                }
            }
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (value != null) {
            checkFormatValue(columnIndex, value);
        }
        values.set(columnIndex, value);
    }

    private void checkFormatValue(int columnIndex, Object value) {
        if (!value.getClass().isAssignableFrom(types.get(columnIndex))) {
            throw new ColumnFormatException("wrong type (Incorrect column format)");
        }

    }

    private void checkIndex(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= types.size()) {
            throw new IndexOutOfBoundsException("wrong type (Incorrect column index)");
        }
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndex(columnIndex);
        return values.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, Integer.class);
        return Integer.class.cast(values.get(columnIndex));
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, Long.class);
        return Long.class.cast(values.get(columnIndex));
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, Byte.class);
        return Byte.class.cast(values.get(columnIndex));
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, Float.class);
        return Float.class.cast(values.get(columnIndex));
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, Double.class);
        return Double.class.cast(values.get(columnIndex));
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, Boolean.class);
        return Boolean.class.cast(values.get(columnIndex));
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, String.class);
        return String.class.cast(values.get(columnIndex));
    }

}
