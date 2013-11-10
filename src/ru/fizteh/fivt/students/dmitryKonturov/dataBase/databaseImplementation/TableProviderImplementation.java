package ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.DatabaseException;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils.CheckDatabasesWorkspace;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils.JsonUtils;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils.MultiFileMapLoaderWriter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableProviderImplementation implements TableProvider {

    private final Path workspace;
    private boolean isLoading = false;
    private Map<String, Table> existingTables;
    static final Class[] ALLOWED_TYPES = new Class[]{
            Integer.class,
            Long.class,
            Byte.class,
            Float.class,
            Double.class,
            String.class,
            Boolean.class
    };

    private boolean isAllowedNameForTable(String tableName) {
        if (tableName == null) {
            return false;
        }
        boolean containDisallowedCharacter = tableName.contains("\\") || tableName.contains("/")
                || tableName.contains(":") || tableName.contains("*")
                || tableName.contains("?") || tableName.contains("\"")
                || tableName.contains("<") || tableName.contains(">")
                || tableName.contains("|");
        return !containDisallowedCharacter;
    }

    TableProviderImplementation(Path path) throws IOException, DatabaseException {
        workspace = path;
        CheckDatabasesWorkspace.checkWorkspace(workspace);
        existingTables = new HashMap<>();
        isLoading = true;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                String tableName = entry.toFile().getName();
                TableImplementation currentTable = new TableImplementation(tableName, this);
                existingTables.put(tableName, currentTable);
            }
        } catch (IOException e) {
            throw new IOException("Fail to load existing base", e);
        } catch (Exception e) {
            throw new DatabaseException("Fail to load existing base", e);
        }
        isLoading = false;
    }

    Path getWorkspace() {
        return workspace;
    }

    boolean isProviderLoading() {
        return isLoading;
    }

    @Override
    public Table getTable(String name) {
        if (!isAllowedNameForTable(name)) {
            throw new IllegalArgumentException("name is null or contains disallowed characters: " + name);
        }
        return existingTables.get(name);
    }

    /**
     * Создаёт таблицу с указанным названием.
     * Создает новую таблицу. Совершает необходимые дисковые операции.
     *
     * @param name        Название таблицы.
     * @param columnTypes Типы колонок таблицы. Не может быть пустой.
     * @return Объект, представляющий таблицу. Если таблица с указанным именем существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение. Если список типов
     *                                  колонок null или содержит недопустимые значения.
     * @throws java.io.IOException      При ошибках ввода/вывода.
     */
    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (!isAllowedNameForTable(name)) {
            throw new IllegalArgumentException("Name is null or contains disallowed characters: " + name);
        }
        for (Class<?> currentType : columnTypes) {
            boolean isAllowed = false;
            for (Class<?> type : ALLOWED_TYPES) {
                if (currentType.equals(type)) {
                    isAllowed = true;
                }
            }
            if (!isAllowed) {
                throw new IllegalArgumentException("Not all column types is allowed");
            }
        }
        Table oldTable =  existingTables.get(name);
        if (oldTable != null) {
            return null;
        }
        Table newTable;
        try {
            newTable = new TableImplementation(name, this, columnTypes);
        } catch (Exception e) {
            throw new IOException("Fail to create database", e);
        }
        existingTables.put(name, newTable);
        return newTable;
    }

    /**
     * Удаляет существующую таблицу с указанным названием.
     * <p/>
     * Объект удаленной таблицы, если был кем-то взят с помощью {@link #getTable(String)},
     * с этого момента должен бросать {@link IllegalStateException}.
     *
     * @param name Название таблицы.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException    Если таблицы с указанным названием не существует.
     * @throws java.io.IOException      - при ошибках ввода/вывода.
     */
    @Override
    public void removeTable(String name) throws IOException {
        if (!isAllowedNameForTable(name)) {
            throw new IllegalArgumentException("Name is null or contains disallowed characters: " + name);
        }
        if (existingTables.get(name) != null) {
            try {
                MultiFileMapLoaderWriter.recursiveRemove(workspace.resolve(name));
                existingTables.remove(name);
            } catch (Exception e) {
                throw new IOException("Cannot remove table", e);
            }
        } else {
            throw new IllegalStateException("Not exists");
        }
    }

    /**
     * Преобразовывает строку в объект {@link ru.fizteh.fivt.storage.structured.Storeable}, соответствующий структуре таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value Строка, из которой нужно прочитать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Прочитанный {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @throws java.text.ParseException - при каких-либо несоответстиях в прочитанных данных.
     */
    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return JsonUtils.deserialize(this, table, value);
    }

    /**
     * Преобразовывает объект {@link ru.fizteh.fivt.storage.structured.Storeable} в строку.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value {@link ru.fizteh.fivt.storage.structured.Storeable}, который нужно записать.
     * @return Строка с записанным значением.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException
     *          При несоответствии типа в {@link ru.fizteh.fivt.storage.structured.Storeable} и типа колонки в таблице.
     */
    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return JsonUtils.serialize(table, value);
    }

    /**
     * Создает новый пустой {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Пустой {@link ru.fizteh.fivt.storage.structured.Storeable}, нацеленный на использование с этой таблицей.
     */
    @Override
    public Storeable createFor(Table table) {
        return new StoreableImplementation(table);
    }

    /**
     * Создает новый {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы, подставляя туда переданные значения.
     *
     * @param table  Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param values Список значений, которыми нужно проинициализировать поля Storeable.
     * @return {@link ru.fizteh.fivt.storage.structured.Storeable}, проинициализированный переданными значениями.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException
     *                                   При несоответствии типа переданного значения и колонки.
     * @throws IndexOutOfBoundsException При несоответствии числа переданных значений и числа колонок.
     */
    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        int valueSize = values.size();
        if (valueSize != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("Requested column count and table column count not match");
        }
        Storeable toReturn = createFor(table);
        for (int i = 0; i < values.size(); ++i) {
            toReturn.setColumnAt(i, values.get(i));
        }
        return toReturn;
    }
}
