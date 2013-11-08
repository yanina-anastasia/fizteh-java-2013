package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

;

public class MyStoreable implements Storeable {

    private final List<Class<?>> myTypes = new ArrayList();
    private List<Object> myValues = new ArrayList();

    public MyStoreable(Table table) {
        for (int i = 0; i < table.getColumnsCount(); i++) {
            myTypes.add(table.getColumnType(i));
            myValues.add(null);
        }
    }

    public int getSize() {
        return myValues.size();
    }
    
    public MyStoreable(Table table, List<?> values)
            throws IndexOutOfBoundsException, ColumnFormatException {
        if (values == null || values.size() != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException(
                    "number of columns and size of values list mismatch");
        }
        for (int i = 0; i < table.getColumnsCount(); i++) {
            if (!table.getColumnType(i).equals(values.get(i).getClass())) {
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

    public void setColumnAt(int columnIndex, Object value)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (!isCorrectIndex(columnIndex)) {
            throw new IndexOutOfBoundsException("incorrect index");
        }
        if (value != null) {
            if (!value.getClass().equals(myTypes.get(columnIndex))) {
                throw new ColumnFormatException(
                        "type of value mismatches type of column");
            }
        } else {
            throw new ColumnFormatException(
                    "setColumnAt: empty type");
        }
        myValues.add(columnIndex, value);
    }

    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        if (!isCorrectIndex(columnIndex)) {
            throw new IndexOutOfBoundsException("incorrect index");
        }
        return myValues.get(columnIndex);
    }

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

    /**
     * Возвращает значение из данной колонки, приведя его к Byte.
     * 
     * @param columnIndex
     *            - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Byte. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException
     *             - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException
     *             - Неверный индекс колонки.
     */
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

    /**
     * Возвращает значение из данной колонки, приведя его к Float.
     * 
     * @param columnIndex
     *            - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Float. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException
     *             - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException
     *             - Неверный индекс колонки.
     */
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

    /**
     * Возвращает значение из данной колонки, приведя его к Double.
     * 
     * @param columnIndex
     *            - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Double. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException
     *             - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException
     *             - Неверный индекс колонки.
     */
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

    /**
     * Возвращает значение из данной колонки, приведя его к Boolean.
     * 
     * @param columnIndex
     *            - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Boolean. Может быть
     *         null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException
     *             - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException
     *             - Неверный индекс колонки.
     */
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

    /**
     * Возвращает значение из данной колонки, приведя его к String.
     * 
     * @param columnIndex
     *            - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к String. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException
     *             - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException
     *             - Неверный индекс колонки.
     */
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
