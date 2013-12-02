package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dubovpavel.proxy.ProxyUtils;

import java.util.ArrayList;

public class StoreableImpl implements Storeable {
    private ArrayList<Object> data;
    private ArrayList<Class<?>> fields;

    public StoreableImpl(ArrayList<Class<?>> types) {
        fields = types;
        data = new ArrayList<>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            data.add(null);
        }
    }

    @Override
    public String toString() {
        StringBuilder internal = new StringBuilder();
        for (Object cell: data) {
            internal.append(cell == null ? "" : cell.toString());
            internal.append(',');
        }
        internal.setLength(internal.length() - 1);
        return ProxyUtils.generateRepr(this, internal.toString());
    }

    private void checkIndex(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= fields.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Index is out of bound. Size: %s, index: %s", fields.size(), columnIndex)
            );
        }
    }

    public int size() {
        return fields.size();
    }

    private void checkType(int columnIndex, Class<?> type) throws ColumnFormatException {
        Class<?> signatureType = fields.get(columnIndex);
        if (!TypeNamesMatcher.CASTABLE_CLASSES.get(signatureType).contains(type)) {
            throw new ColumnFormatException(
                    String.format("Types mismatch. Expected: %s, found: %s",
                            fields.get(columnIndex).getName(), type.getName())
            );
        }
    }

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        if (value != null) {
            checkType(columnIndex, value.getClass());
        }
        data.set(columnIndex, value);
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkIndex(columnIndex);
        return fields.get(columnIndex).cast(data.get(columnIndex));
    }

    private <T> T getCastedAt(int columnIndex, Class<T> tClass)
            throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkType(columnIndex, tClass);
        return (T) data.get(columnIndex);
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastedAt(columnIndex, Integer.class);
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastedAt(columnIndex, Long.class);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastedAt(columnIndex, Byte.class);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastedAt(columnIndex, Float.class);
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastedAt(columnIndex, Double.class);
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastedAt(columnIndex, Boolean.class);
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return getCastedAt(columnIndex, String.class);
    }
}
