package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.ArrayList;
import java.util.List;

public class FileMapStoreable implements Storeable {

    private List<Object> column = new ArrayList<>();
    private List<Class<?>> columnType = new ArrayList<>();

    FileMapStoreable(List<Class<?>> columnType) {
        this.columnType = columnType;
        for (Class<?> col : columnType) {
            column.add(null);
        }
    }

    private void checkColumnIndexBounds(int columnIndex) {
        if (columnIndex < 0 || columnIndex > column.size()) {
            throw new IndexOutOfBoundsException("index is out of bounds");
        }
    }

    private void checkColumnFormat(int columnIndex, Class<?> valueClass) {
        if (!valueClass.equals(columnType.get(columnIndex))) {
            throw new ColumnFormatException("wrong column format");
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, value.getClass());
        column.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        return column.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Integer.class);
        return (Integer) column.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Long.class);
        return (Long) column.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Byte.class);
        return (Byte) column.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Float.class);
        return (Float) column.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Double.class);
        return (Double) column.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, Boolean.class);
        return (Boolean) column.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkColumnIndexBounds(columnIndex);
        checkColumnFormat(columnIndex, String.class);
        return (String) column.get(columnIndex);
    }

    public String messageEqualsType(List<Class<?>> list) {
        if (list.size() != columnType.size()) {
            return "wrong type count of arguments different";
        }
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getName().equals(columnType.get(i).getName())) {
                return String.format("wrong type %s not exist", list.get(i).getName());
            }
        }
        return "";
    }

    public boolean equals(Object obj) {
        if (!obj.getClass().getName().equals(this.getClass().getName())) {
            return false;
        }
        FileMapStoreable line = (FileMapStoreable) obj;
        for (int i = 0; i < line.columnType.size(); i++) {
            if (!line.columnType.get(i).getName().equals(columnType.get(i).getName())) {
                return false;
            }
        }
        for (int i = 0; i < line.column.size(); i++) {
            switch (columnType.get(i).getName()) {
                case "java.lang.Integer":
                    if (line.getIntAt(i) != null && this.getIntAt(i) != null) {
                        if (!line.getIntAt(i).equals(this.getIntAt(i))) {
                            return false;
                        }
                    }
                    break;
                case "java.lang.Long":
                    if (line.getLongAt(i) != null && this.getLongAt(i) != null) {
                        if (!line.getLongAt(i).equals(this.getLongAt(i))) {
                            return false;
                        }
                    }
                    break;
                case "java.lang.Byte":
                    if (line.getByteAt(i) != null && this.getByteAt(i) != null) {
                        if (!line.getByteAt(i).equals(this.getByteAt(i))) {
                            return false;
                        }
                    }
                    break;
                case "java.lang.Float":
                    if (line.getFloatAt(i) != null && this.getFloatAt(i) != null) {
                        if (!line.getFloatAt(i).equals(this.getFloatAt(i))) {
                            return false;
                        }
                    }
                    break;
                case "java.lang.Double":
                    if (line.getDoubleAt(i) != null && this.getDoubleAt(i) != null) {
                        if (!line.getDoubleAt(i).equals(this.getDoubleAt(i))) {
                            return false;
                        }
                    }
                    break;
                case "java.lang.Boolean":
                    if (line.getBooleanAt(i) != null && this.getBooleanAt(i) != null) {
                        if (!line.getBooleanAt(i).equals(this.getBooleanAt(i))) {
                            return false;
                        }
                    }
                    break;
                case "java.lang.String":
                    if (line.getStringAt(i) != null && this.getStringAt(i) != null) {
                        if (!line.getStringAt(i).equals(this.getStringAt(i))) {
                            return false;
                        }
                    }
                    break;
            }
        }
        return true;
    }
}
