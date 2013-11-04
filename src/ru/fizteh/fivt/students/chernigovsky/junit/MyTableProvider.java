package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.MultiFileHashMapUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MyTableProvider implements TableProvider {
    private static final String TABLE_NAME_FORMAT = "[A-Za-zА-Яа-я0-9]+";
    private HashMap<String, MyTable> tableHashMap;
    private File dbDirectory;

    public MyTableProvider(File newDbDirectory) {
        dbDirectory = newDbDirectory;
        tableHashMap = new HashMap<String, MyTable>();
        for (String string : dbDirectory.list()) {
            tableHashMap.put(string, new MyTable(string));
        }
    }

    public File getDbDirectory() {
        return dbDirectory;
    }

    /**
     * Возвращает таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблицы с указанным именем не существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("wrong table name");
        }

        return tableHashMap.get(name);
    }

    /**
     * Создаёт таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблица уже существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("wrong table name");
        }

        if (tableHashMap.get(name) != null) {
            return null;
        }

        File tableDirectory = new File(getDbDirectory(), name);
        if (!tableDirectory.mkdir()) {
            throw new IllegalArgumentException("directory making error");
        }

        MyTable newTable = new MyTable(name);

        tableHashMap.put(name, newTable);
        return newTable;
    }

    /**
     * Удаляет таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException Если таблицы с указанным названием не существует.
     */
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("wrong table name");
        }

        if (tableHashMap.get(name) == null) {
            throw new IllegalStateException("no such table");
        }

        tableHashMap.remove(name);
        File tableDirectory = new File(getDbDirectory(), name);

        try {
            MultiFileHashMapUtils.delete(tableDirectory);
        } catch (IOException ex) {
            throw new IllegalArgumentException("directory removal error");
        }

    }
}
