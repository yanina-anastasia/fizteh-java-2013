package ru.fizteh.fivt.students.chernigovsky.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.chernigovsky.junit.AbstractTable;

import java.util.List;

public class StoreableTable extends AbstractTable<Storeable> implements ExtendedStoreableTable {
    List<Class<?>> columnTypeList;

    public StoreableTable(String name, boolean flag, List<Class<?>> newColumnTypeList) {
        super(name, flag);
        columnTypeList = newColumnTypeList;
    }

    public int getColumnsCount() {
        return columnTypeList.size();
    }

    /**
     * Возвращает тип значений в колонке.
     *
     * @param columnIndex Индекс колонки. Начинается с нуля.
     * @return Класс, представляющий тип значения.
     *
     * @throws IndexOutOfBoundsException - неверный индекс колонки
     */
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return columnTypeList.get(columnIndex);
    }

    public Storeable put(String key, Storeable value) {
        if (value == null) { // maybe need to check: value.trim().isEmpty()
            throw new IllegalArgumentException("value is null");
        }
        StoreableUtils.checkValue(this, value);
        return super.put(key, value);
    }
}
