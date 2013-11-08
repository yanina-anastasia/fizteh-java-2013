package ru.fizteh.fivt.students.surakshina.filemap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class MyStoreable implements Storeable {
    private ArrayList<Object> values = null;
    private ArrayList<Class<?>> types = null;
    private Table currentTable;

    public MyStoreable(Table table) {
        this.currentTable = table;
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
            return true;
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

    public MyStoreable(Table table, List<?> values2) {
        int size = values.size();
        if (size != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("Incorrect number of values");
        }
        values = new ArrayList<Object>(currentTable.getColumnsCount());
        types = new ArrayList<Class<?>>(currentTable.getColumnsCount());
        for (int i = 0; i < currentTable.getColumnsCount(); ++i) {
            types.add(currentTable.getColumnType(i));
            if (values2.get(i) != JSONObject.NULL && !currentTable.getColumnType(i).equals(values2.get(i).getClass())) {
                throw new ColumnFormatException("Incorrect column type");
            }
            values.add(values2.get(i));
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
        if (!value.getClass().equals(types.get(columnIndex))) {
            throw new ColumnFormatException("Incorrect column format");
        }

    }

    private void checkIndex(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= types.size()) {
            throw new IndexOutOfBoundsException("Incorrect column index");
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
        return (Integer) values.get(columnIndex);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, Long.class);
        return (Long) values.get(columnIndex);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, Byte.class);
        return (Byte) values.get(columnIndex);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, Float.class);
        return (Float) values.get(columnIndex);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, Double.class);
        return (Double) values.get(columnIndex);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkIndex(columnIndex);
        checkFormatValue(columnIndex, Boolean.class);
        return (Boolean) values.get(columnIndex);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {

        checkIndex(columnIndex);
        checkFormatValue(columnIndex, String.class);
        return (String) values.get(columnIndex);
    }

}
