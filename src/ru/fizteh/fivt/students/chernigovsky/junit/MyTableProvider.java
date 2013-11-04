package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.chernigovsky.filemap.StateProvider;
import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.MultiFileHashMapUtils;

import java.io.File;
import java.io.IOException;

public class MyTableProvider extends StateProvider implements TableProvider {
    private static final String TABLE_NAME_FORMAT = "[A-Za-zА-Яа-я0-9]+";

    public MyTableProvider(File dbDirectory) {
        super(dbDirectory);
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

        File tableDirectory = new File(getDbDirectory(), name);
        if (tableDirectory.exists() && tableDirectory.isDirectory()) {
            return new MyTable(name);
        }
        return null;
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

        File tableDirectory = new File(getDbDirectory(), name);
        if (tableDirectory.exists() && tableDirectory.isDirectory()) {
            return null;
        }
        if (!tableDirectory.mkdir()) {
            throw new IllegalArgumentException("directory making error");
        }
        return new MyTable(name);
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

        File tableDirectory = new File(getDbDirectory(), name);
        if (!tableDirectory.exists() || !tableDirectory.isDirectory()) {
            throw new IllegalStateException("no such table");
        }

        try {
            MultiFileHashMapUtils.delete(tableDirectory);
        } catch (IOException ex) {
            throw new IllegalArgumentException("directory removal error");
        }

    }
}
