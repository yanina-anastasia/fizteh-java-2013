package ru.fizteh.fivt.students.mikhaylova_daria.db;

import ru.fizteh.fivt.storage.structured.*;

import java.util.ArrayList;
import java.util.HashMap;


public class Value implements Storeable {

    private ArrayList<Object> value;
    private ArrayList<Class<?>> types;

    private static Class<?> normType(String arg) {
        HashMap<String, Class<?>> types = new HashMap<>();
        types.put("Integer", Integer.class);
        types.put("Long", Long.class);
        types.put("Double", Double.class);
        types.put("Float", Float.class);
        types.put("Boolean", Boolean.class);
        types.put("Byte", Byte.class);
        types.put("byte", Byte.class);
        types.put("String", String.class);
        types.put("int", Integer.class);
        types.put("long", Long.class);
        types.put("double", Double.class);
        types.put("float", Float.class);
        types.put("boolean", Boolean.class);
        return types.get(arg);
    }


    Value(Table table) {
        System.out.println(this.toString());

        types = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(normType(table.getColumnType(i).getSimpleName()));
        }
        value = new ArrayList<>(types.size());
        for (int i = 0; i < types.size(); ++i) {
            if (types.get(i) == null) {
                throw new IllegalArgumentException("wrong type (bad Table: the table contains null type)");
            }
            if (types.get(i).equals(Integer.class)) {
                value.add(i, Integer.MIN_VALUE);
            } else {
                if (types.get(i).equals(Long.class)) {
                    value.add(i, Long.MIN_VALUE);
                } else {
                    if (types.get(i).equals(Byte.class)) {
                        value.add(i, Byte.MIN_VALUE);
                    } else {
                        if (types.get(i).equals(Float.class)) {
                            value.add(i, Float.MIN_VALUE);
                        } else {
                            if (types.get(i).equals(Double.class)) {
                                value.add(i, Double.MIN_VALUE);
                            } else {
                                if (types.get(i).equals(String.class)) {
                                    value.add(i, "default");
                                } else {
                                    if (types.get(i).equals(Boolean.class)) {
                                        value.add(i, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            System.out.println(value.get(i).toString() + " " + i);
        }
    }

    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.size() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!(value == null)) {
            if (normType(value.getClass().getSimpleName()) == null) {
                throw new ColumnFormatException("This type is not supposed: " + value.getClass().getCanonicalName());
            }
            if (!value.getClass().equals(types.get(columnIndex))) {
                throw new ColumnFormatException("Wrong type of value: " + value.getClass()
                        + " but expected "  + types.get(columnIndex));
            }
        }
        this.value.add(columnIndex, value);
    }


    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (types.size() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column: " + columnIndex);
        }
        return value.get(columnIndex);
    }


    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.size() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column: " + columnIndex);
        }
        if (!types.get(columnIndex).equals(Integer.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + types.get(columnIndex).getCanonicalName());
        }
        Integer integer = null;
        if (value.get(columnIndex) != null) {
            System.out.println(columnIndex);
            integer = (Integer) value.get(columnIndex - 1);
        }
        return integer;
    }


    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.size() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!types.get(columnIndex).equals(Long.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + types.get(columnIndex).getCanonicalName());
        }
        Long val = null;
        if (value.get(columnIndex) != null) {
            val = (Long) value.get(columnIndex);
        }
        return val;
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.size() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!types.get(columnIndex).equals(Byte.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + types.get(columnIndex).getCanonicalName());
        }
        Byte val = null;
        if (value.get(columnIndex) != null) {
            val = (Byte) value.get(columnIndex);
        }
        return val;
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.size() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!types.get(columnIndex).equals(Float.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + types.get(columnIndex).getCanonicalName());
        }
        Float val = null;
        if (value.get(columnIndex) != null) {
            val = (Float) value.get(columnIndex);
        }
        return val;
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.size() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!types.get(columnIndex).equals(Double.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + types.get(columnIndex).getCanonicalName());
        }
        Double val = null;
        if (value.get(columnIndex) != null) {
            val = (Double) value.get(columnIndex);
        }
        return val;
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.size() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!types.get(columnIndex).equals(Boolean.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + types.get(columnIndex).getCanonicalName());
        }
        Boolean val = null;
        if (value.get(columnIndex) != null) {
            val = (Boolean) value.get(columnIndex);
        }
        return val;
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (types.size() <= columnIndex) {
            throw new IndexOutOfBoundsException("Wrong index of column " + columnIndex);
        }
        if (!types.get(columnIndex).equals(String.class)) {
            throw new ColumnFormatException("Type of this column is "
                    + types.get(columnIndex).getCanonicalName());
        }
        String val = null;
        if (value.get(columnIndex) != null) {
            val = (String) value.get(columnIndex);
        }
        return val;
    }

}
