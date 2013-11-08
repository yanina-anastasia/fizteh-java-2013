package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.storage.structured.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Список фиксированной структуры, строка таблицы {@link ru.fizteh.fivt.storage.structured.Table}.
 *
 * Нумерация колонок с нуля. Позиция в списке соответствует колонке таблицы под тем же номером.
 *
 * С помощью {@link ru.fizteh.fivt.storage.structured.TableProvider} может быть сериализован или десериализован.
 *
 * Для получения объекта из нужной колонки воспользуйтесь соответствующим геттером.
 * Для установки объекта а колонку воспользуйтесь {@link #setColumnAt(int, Object)} .
 */

public class TableRow implements Storeable {

    private List<Class<?>> classes;
    private List<Object> columns;

    TableRow(List<Class<?>> classes) {
        this.classes = classes;
        columns = new ArrayList<>(classes.size());
    }

    TableRow(List<Class<?>> classes, List<Object> columns) {
        this.classes = classes;
        for (int i = 0; i < classes.size(); ++i) {
            if (!columns.get(i).getClass().equals(classes.get(i))) {
                throw new ColumnFormatException("Can't init Storeable column: incorrect type");
            }
        }
        this.columns = columns;
    }

    /**
     * Установить значение в колонку
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @param value - значение, которое нужно установить.
     *              Может быть null.
     *              Тип значения должен соответствовать декларированному типу колонки.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Тип значения не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        if (value == null) {
            throw new ColumnFormatException("Stored data can't be null");
        }
        checkType(columnIndex, value.getClass());
        if (value instanceof String) {
            if (((String) value).trim().equals("")) {
                throw new ColumnFormatException("Empty string can't be stored");
            }
        }
        columns.add(columnIndex, value);
    }

    /**
     * Возвращает значение из данной колонки, не приводя его к конкретному типу.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, без приведения типа. Может быть null.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        checkBounds(columnIndex);
        return columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Integer.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Integer. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Integer.class);
        return (Integer) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Long.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Long. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Long.class);
        return (Long) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Byte.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Byte. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Byte.class);
        return (Byte) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Float.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Float. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Float.class);
        return (Float) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Double.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Double. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Double.class);
        return (Double) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к Boolean.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к Boolean. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, Boolean.class);
        return (Boolean) columns.get(columnIndex);
    }

    /**
     * Возвращает значение из данной колонки, приведя его к String.
     * @param columnIndex - индекс колонки в таблице, начиная с нуля
     * @return - значение в этой колонке, приведенное к String. Может быть null.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException - Запрошенный тип не соответствует типу колонки.
     * @throws IndexOutOfBoundsException - Неверный индекс колонки.
     */
    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        checkBounds(columnIndex);
        checkType(columnIndex, String.class);
        return (String) columns.get(columnIndex);
    }

    private void checkBounds(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= classes.size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void checkType(int columnIndex, Class<?> value) {
        if (!value.equals(classes.get(columnIndex))) {
            throw new ColumnFormatException("Wrong column type. was: "
                    + value.toString() + "; expected: " + classes.get(columnIndex));
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < classes.size(); ++i) {
            switch (classes.get(i).getName()) {
                case "java.lang.Integer":
                    stringBuilder.append(getIntAt(i) + " ");
                    break;
                case "java.lang.Long":
                    stringBuilder.append(getLongAt(i) + " ");
                    break;
                case "java.lang.Byte":
                    stringBuilder.append(getByteAt(i) + " ");
                    break;
                case "java.lang.Float":
                    stringBuilder.append(getFloatAt(i) + " ");
                    break;
                case "java.lang.Double":
                    stringBuilder.append(getDoubleAt(i) + " ");
                    break;
                case "java.lang.Boolean":
                    stringBuilder.append(getBooleanAt(i) + " ");
                    break;
                case "java.lang.String":
                    stringBuilder.append(getStringAt(i) + " ");
                    break;
                default:
                    throw new ColumnFormatException("Uknonwn column type: " + classes.get(i).getName());
            }
        }
        return stringBuilder.toString();
    }
}
