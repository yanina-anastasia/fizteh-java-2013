package ru.fizteh.fivt.students.chernigovsky.storeable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.chernigovsky.junit.AbstractTableProvider;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoreableTableProvider extends AbstractTableProvider<ExtendedStoreableTable> implements ExtendedStoreableTableProvider {
    public StoreableTableProvider(File newDbDirectory, boolean flag) {
        super(newDbDirectory, flag);
        if (newDbDirectory != null) {
            for (String string : newDbDirectory.list()) {
                ExtendedStoreableTable newTable = new StoreableTable(string, flag, null);
                tableHashMap.put(string, newTable);
                try {
                    StoreableUtils.readTable(newTable, this);
                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }
        }
    }

    /**
     * Создаёт таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблица уже существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    public ExtendedStoreableTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
    if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("wrong table name");
        }

        if (tableHashMap.get(name) != null) {
            return null;
        }

        if (columnTypes == null) {
            throw new IllegalArgumentException("columnTypes is null");
        }

        if (columnTypes.isEmpty()) {
            throw new IllegalArgumentException("ColumnTypes list is empty");
        }

        File tableDirectory = new File(getDbDirectory(), name);
        if (!tableDirectory.mkdir()) {
            throw new IllegalArgumentException("directory making error");
        }

        File signature = new File(tableDirectory, "signature.tsv");
        if (!signature.createNewFile()) {
            throw new IllegalArgumentException("signature making error");
        }

        FileOutputStream fileOutputStream = new FileOutputStream(signature);
        fileOutputStream.getChannel().truncate(0); // Clear file
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
        try {
            for (Class<?> type : columnTypes) {
                if (type == null) {
                    throw new IllegalArgumentException("wrong column type");
                }
                TypeEnum typesEnum = TypeEnum.getByClass(type);
                if (typesEnum == null) {
                    throw new IOException("write error");
                }
                String typeString = TypeEnum.getByClass(type).getSignature();
                dataOutputStream.write(typeString.getBytes("UTF-8"));
                dataOutputStream.write(' ');
            }

        } finally {
            dataOutputStream.close();
        }

        ExtendedStoreableTable newTable = new StoreableTable(name, autoCommit, columnTypes);

        tableHashMap.put(name, newTable);
        return newTable;
    }

    /**
     * Преобразовывает строку в объект {@link ru.fizteh.fivt.storage.structured.Storeable}, соответствующий структуре таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value Строка, из которой нужно прочитать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Прочитанный {@link ru.fizteh.fivt.storage.structured.Storeable}.
     *
     * @throws java.text.ParseException - при каких-либо несоответстиях в прочитанных данных.
     */
    public MyStoreable deserialize(Table table, String value) throws ParseException {
        /* if (table == null) {
            throw new IllegalArgumentException();
        }

        if (value == null) {
            return null;
        } maybe need this checks */

        JSONArray array;
        try {
            array = new JSONArray(value);
        } catch (JSONException ex) {
            throw new ParseException("JSONArray create error", -1);
        }

        if (array.length() != table.getColumnsCount()) {
            throw new ParseException("incorrect value", -1);
        }

        MyStoreable result = createFor(table);
        for (int i = 0; i < array.length(); ++i) {
            try {
                Object object = array.get(i);
                if (object == JSONObject.NULL) {
                    result.setColumnAt(i, null);
                } else {
                    if (table.getColumnType(i) == Long.class) {
                        result.setColumnAt(i, Long.valueOf(object.toString()));
                    } else if (table.getColumnType(i) == Float.class) {
                        result.setColumnAt(i, Float.valueOf(object.toString()));
                    } else if (table.getColumnType(i) == Byte.class) {
                        result.setColumnAt(i, Byte.valueOf(object.toString()));
                    } else {
                        result.setColumnAt(i, table.getColumnType(i).cast(object));
                    }
                }
            }  catch (ColumnFormatException | IndexOutOfBoundsException e) {
                throw new ParseException("JSON: incorrect format", i);
            }
        }

        return result;
    }

    private static boolean isCorrectSize(Storeable value, Table table) {
        try {
            value.getColumnAt(table.getColumnsCount() - 1);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        try {
            value.getColumnAt(table.getColumnsCount());
        }  catch (IndexOutOfBoundsException e) {
            return true;
        }

        return false;
    }

    public static boolean isCorrectStoreable(Storeable value, Table table) {
        if (value == null) {
            throw new IllegalArgumentException("Storeable must be not null");
        }
        if (table == null) {
            throw new IllegalArgumentException("Table must be not null");
        }

        if (!isCorrectSize(value, table)) {
            return false;
        }

        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (value.getColumnAt(i) != null
                    && value.getColumnAt(i).getClass() != table.getColumnType(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Преобразовывает объект {@link ru.fizteh.fivt.storage.structured.Storeable} в строку.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value {@link ru.fizteh.fivt.storage.structured.Storeable}, который нужно записать.
     * @return Строка с записанным значением.
     *
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException При несоответствии типа в {@link ru.fizteh.fivt.storage.structured.Storeable} и типа колонки в таблице.
     */
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        /*Object[] serializedValue = new Object[table.getColumnsCount()];
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            serializedValue[i] = value.getColumnAt(i);
        }
        JSONArray array = new JSONArray(serializedValue);
        return array.toString(); */
        if (value == null) {
            throw new IllegalArgumentException("Storeable must be not null");
        }
        if (table == null) {
            throw new IllegalArgumentException("Table must be not null");
        }

        if (!isCorrectStoreable(value, table)) {
            throw new ColumnFormatException("Incorrect storeable");
        }

        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (value.getColumnAt(i) != null) {
                if (table.getColumnType(i) == String.class) {
                    result.append("\"");
                    result.append(value.getStringAt(i));
                    result.append("\"");
                } else {
                    result.append(value.getColumnAt(i).toString());
                }
            } else {
                result.append("null");
            }

            if (i != table.getColumnsCount() - 1) {
                result.append(",");
            }  else {
                result.append("]");
            }
        }

        return result.toString();
    }

    /**
     * Создает новый пустой {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Пустой {@link ru.fizteh.fivt.storage.structured.Storeable}, нацеленный на использование с этой таблицей.
     */
    public MyStoreable createFor(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("null table");
        }
        return new MyStoreable(table);
    }

    /**
     * Создает новый {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы, подставляя туда переданные значения.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param values Список значений, которыми нужно проинициализировать поля Storeable.
     * @return {@link ru.fizteh.fivt.storage.structured.Storeable}, проинициализированный переданными значениями.
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException При несоответствии типа переданного значения и колонки.
     * @throws IndexOutOfBoundsException При несоответствии числа переданных значений и числа колонок.
     */
    public MyStoreable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table == null || values == null || values.isEmpty()) {
            throw new IllegalArgumentException("null value or table");
        }

        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException("invalid values count");
        }

        MyStoreable storeable = new MyStoreable(table);
        for (int i = 0; i < values.size(); ++i) {
            storeable.setColumnAt(i, values.get(i));
        }

        return storeable;
    }
}
