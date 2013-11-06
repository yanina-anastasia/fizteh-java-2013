package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseStoreable implements Storeable {
    List<Class<?>> classes = new ArrayList<>();
    List<Object> columns = new ArrayList<>();

    public DatabaseStoreable(List<Class<?>> classes) {
        this.classes = classes;

        for (int index = 0; index < classes.size(); ++index) {
            columns.add(null);
        }
    }

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        if (value != null) {
            checkColumnType(columnIndex, value.getClass());
            try {
                if (value == null) {
                    return;
                }

                switch (formatColumnType(value.getClass())) {
                    case "String":
                        String stringValue = (String) value;
                        if (stringValue.trim().isEmpty()) {
                            throw new ParseException("value cannot be null", 0);
                        }
                        break;
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException("incorrect value");
            }
        }
        columns.set(columnIndex, value);
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkBounds(columnIndex);

        return columns.get(columnIndex);
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Integer.class);

        return (Integer) columns.get(columnIndex);
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Long.class);

        return (Long) columns.get(columnIndex);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Byte.class);

        return (Byte) columns.get(columnIndex);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Float.class);

        return (Float) columns.get(columnIndex);
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Double.class);

        return (Double) columns.get(columnIndex);
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, Boolean.class);

        return (Boolean) columns.get(columnIndex);
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkColumnType(columnIndex, String.class);

        return (String) columns.get(columnIndex);
    }

    public void addColumn(Class<?> columnType) {
        classes.add(columnType);
        columns.add(null);
    }

    public void setColumns(List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values.size() != classes.size()) {
            throw new IndexOutOfBoundsException();
        }

        columns.clear();

        for (int index = 0; index < values.size(); ++index) {
            columns.add(values.get(index));
        }
    }

    private void checkBounds(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= classes.size()) {
            throw new IndexOutOfBoundsException(String.format("index out of bound: %d", index));
        }
    }

    private void checkColumnType(int columnIndex, Object value) throws ColumnFormatException {
        if (!value.getClass().isAssignableFrom(classes.get(columnIndex))) {
            throw new ColumnFormatException(String.format("incorrect type: expected type: %s actual type: %s",
                    classes.get(columnIndex).getName(), value.getClass().getName()));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final Object listEntry : columns) {
            if (!first) {
                sb.append(" ");
            }
            first = false;
            if (listEntry == null) {
                sb.append("null");
            } else {
                sb.append(listEntry.toString());
            }
        }
        return sb.toString();
    }

    public String formatColumnType(Class<?> columnType) {
        switch (columnType.getName()) {
            case "java.lang.Integer":
                return "int";
            case "java.lang.Long":
                return "long";
            case "java.lang.Byte":
                return "byte";
            case "java.lang.Float":
                return "float";
            case "java.lang.Double":
                return "double";
            case "java.lang.Boolean":
                return "boolean";
            case "java.lang.String":
                return "String";
            default:
                return null;
        }
    }
}