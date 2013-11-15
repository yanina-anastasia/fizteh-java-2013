package ru.fizteh.fivt.students.dzvonarev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.ArrayList;
import java.util.List;

public class MyStoreable implements Storeable {

    public MyStoreable(Table table, List<?> args) throws IndexOutOfBoundsException, ColumnFormatException {
        columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            columnTypes.add(table.getColumnType(i));
        }
        if (args.size() != columnTypes.size()) {            // wrong count of columns
            throw new IndexOutOfBoundsException("wrong type (wrong count of columns in value - " + args.size() + ")");
        }
        for (int i = 0; i < args.size(); ++i) {
            if (args.get(i) == null) {
                continue;
            }
            if (args.get(i).getClass() != columnTypes.get(i)) {
                throw new ColumnFormatException("wrong type (" + i + " column got wrong type - " + args.get(i) + ")");
            }
        }
        ArrayList<Object> temp = new ArrayList<>();      // if all OK we init column array
        for (Object arg : args) {
            temp.add(arg);
        }
        column = temp;
    }

    public MyStoreable(Table table) {
        column = new ArrayList<>();
        columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            columnTypes.add(table.getColumnType(i));
            column.add(null);
        }
    }

    private ArrayList<Object> column;           // values in columns
    private ArrayList<Class<?>> columnTypes;    // types of these values


    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (value.getClass() != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("wrong type (value " + value + " got invalid type in " + columnIndex + " column)");
        }
        column.set(columnIndex, value);
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        return column.get(columnIndex);
    }

    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (column.get(columnIndex).getClass() != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Integer) column.get(columnIndex);
    }

    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (column.get(columnIndex).getClass() != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Long) column.get(columnIndex);
    }

    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (column.get(columnIndex).getClass() != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Byte) column.get(columnIndex);
    }

    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (column.get(columnIndex).getClass() != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Float) column.get(columnIndex);
    }

    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (column.get(columnIndex).getClass() != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Double) column.get(columnIndex);
    }

    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (column.get(columnIndex).getClass() != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (Boolean) column.get(columnIndex);
    }

    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= column.size()) {
            throw new IndexOutOfBoundsException("wrong type (wrong column index - " + columnIndex + ")");
        }
        if (column.get(columnIndex).getClass() != columnTypes.get(columnIndex)) {
            throw new ColumnFormatException("wrong type (wrong type of value in " + columnIndex + " column)");
        }
        return (String) column.get(columnIndex);
    }

}
