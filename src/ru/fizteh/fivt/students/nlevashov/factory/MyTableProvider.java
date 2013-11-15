package ru.fizteh.fivt.students.nlevashov.factory;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.nlevashov.shell.Shell;
import ru.fizteh.fivt.students.nlevashov.storable.Storable;

import java.io.BufferedOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Управляющий класс для работы с {@link ru.fizteh.fivt.storage.structured.Table таблицами}
 *
 * Предполагает, что актуальная версия с устройства хранения, сохраняется при создании
 * экземпляра объекта. Далее ввод-вывод выполняется только в момент создания и удаления
 * таблиц.
 *
 * Данный интерфейс не является потокобезопасным.
 */
public class MyTableProvider implements TableProvider {

    HashMap<String, Table> tables;
    Path dbPath;

    /**
     * Конструктор. Составляет список таблиц в базе
     *
     * @param path Адрес базы данных.
     */
    public MyTableProvider(Path path) throws IOException {
        tables = new HashMap<>();
        dbPath = path;
        String name = path.getFileName().toString();
        if ((name == null) || name.trim().isEmpty() || name.matches(".*[/:\\*\\?\"\\\\><\\|\\s\\t\\n].*")) {
        //допилить!!!
        //        || name.contains("/") || name.contains(":") || name.contains("*")
        //        || name.contains("?") || name.contains("\"") || name.contains("\\")
        //        || name.contains(">") || name.contains("<") || name.contains("|")
        //        || name.contains(" ") || name.contains("\t") || name.contains("\n")) {
            throw new IllegalArgumentException("TableProvider.constructor: bad table name \"" + name + "\"");
        }
        if (!Files.exists(dbPath)) {
            if (!path.toFile().getCanonicalFile().mkdir()) {
                throw new IOException("Directory \"" + path.getFileName() + "\" wasn't created");
            }
        } else if (!Files.isDirectory(dbPath)) {
            throw new IllegalArgumentException("TableProvider.constructor: object with said path isn't a directory");
        }
        try {
            checkDataBaseDirectory();
            DirectoryStream<Path> stream = Files.newDirectoryStream(path);
            for (Path f : stream) {
                tables.put(f.getFileName().toString(), (new MyTable(f, this)));
            }
        } catch (IOException e) {
            throw new RuntimeException("TableProvider.constructor: the structure of database is wrong. Specifically, "
                    + e.getMessage());
        }
    }

    /**
     * Возвращает таблицу с указанным названием.
     *
     * Последовательные вызовы метода с одинаковыми аргументами должны возвращать один и тот же объект таблицы,
     * если он не был удален с помощью {@link #removeTable(String)}.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблицы с указанным именем не существует, возвращает null.
     *
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    @Override
    public Table getTable(String name) {
        if ((name == null) || name.trim().isEmpty() || name.matches(".*[/:\\*\\?\"\\\\><\\|\\s\\t\\n].*")) {
        //        || name.contains("/") || name.contains(":") || name.contains("*")
          //      || name.contains("?") || name.contains("\"") || name.contains("\\")
            //    || name.contains(">") || name.contains("<") || name.contains("|")
              //  || name.contains(" ") || name.contains("\\t") || name.contains("\\n")) {
            throw new IllegalArgumentException("TableProvider.getTable: bad table name \"" + name + "\"");
        }
        return tables.get(name);
    }

    /**
     * Создаёт таблицу с указанным названием.
     * Создает новую таблицу. Совершает необходимые дисковые операции.
     *
     * @param name Название таблицы.
     * @param columnTypes Типы колонок таблицы. Не может быть пустой.
     * @return Объект, представляющий таблицу. Если таблица с указанным именем существует, возвращает null.
     *
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение. Если список типов
     *                                  колонок null или содержит недопустимые значения.
     * @throws java.io.IOException При ошибках ввода/вывода.
     */
    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if ((name == null) || name.trim().isEmpty() || name.matches(".*[/:\\*\\?\"\\\\><\\|\\s\\t\\n].*")) {
        //        || name.contains("/") || name.contains(":") || name.contains("*")
          //      || name.contains("?") || name.contains("\"") || name.contains("\\")
            //    || name.contains(">") || name.contains("<") || name.contains("|")
              //  || name.contains(" ") || name.contains("\t") || name.contains("\n")) {
            throw new IllegalArgumentException("TableProvider.createTable: bad table name \"" + name + "\"");
        }
        if ((columnTypes == null) || (columnTypes.isEmpty())) {
            throw new IllegalArgumentException("TableProvider.createTable: columnTypes is null");
        }
        for (Class<?> c : columnTypes)  {
            if (c == null) {
                throw new IllegalArgumentException("TableProvider.createTable: Illegal type \"null\"");
            }
            if ((c != Integer.class) && (c != Long.class) && (c != Byte.class) && (c != Float.class)
                                     && (c != Double.class) && (c != Boolean.class) && (c != String.class)) {
                throw new IllegalArgumentException("TableProvider.createTable: Illegal type \"" + c.toString() + "\"");
            }
        }
        if (tables.containsKey(name)) {
            return null;
        } else {
            try {
                Shell.cd(dbPath.toString());
                Shell.mkdir(name);
            } catch (IOException e) {
                throw new IOException("TableProvider.createTable: directory making error with message \""
                        + e.getMessage() + "\"", e);
            }
            try (BufferedOutputStream o = new BufferedOutputStream(
                                              Files.newOutputStream(dbPath.resolve(name).resolve("signature.tsv")))) {
                for (Class<?> c : columnTypes)  {
                    if (c == Integer.class) {
                        o.write("int".getBytes());
                        o.write(' ');
                    } else if (c == Long.class)  {
                        o.write("long".getBytes());
                        o.write(' ');
                    } else if (c == Byte.class) {
                        o.write("byte".getBytes());
                        o.write(' ');
                    } else if (c == Float.class) {
                        o.write("float".getBytes());
                        o.write(' ');
                    } else if (c == Double.class) {
                        o.write("double".getBytes());
                        o.write(' ');
                    } else if (c == Boolean.class) {
                        o.write("boolean".getBytes());
                        o.write(' ');
                    } else {
                        o.write("String".getBytes());
                        o.write(' ');
                    }
                }
            } catch (IOException e) {
                throw new IOException("TableProvider.createTable: making of \"signature.tsv\" error with message \""
                        + e.getMessage() + "\"", e);
            }
            Table newTable = new MyTable(dbPath.resolve(name), this);
            tables.put(name, newTable);
            return newTable;
        }
    }

    /**
     * Удаляет существующую таблицу с указанным названием.
     *
     * Объект удаленной таблицы, если был кем-то взят с помощью {@link #getTable(String)},
     * с этого момента должен бросать {@link IllegalStateException}.
     *
     * @param name Название таблицы.
     *
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException Если таблицы с указанным названием не существует.
     * @throws java.io.IOException - при ошибках ввода/вывода.
     */
    @Override
    public void removeTable(String name) throws IOException {
        if ((name == null) || name.trim().isEmpty() || name.matches(".*[/:\\*\\?\"\\\\><\\|\\s\\t\\n].*")) {
        //        || name.contains("/") || name.contains(":") || name.contains("*")
          //      || name.contains("?") || name.contains("\"") || name.contains("\\")
            //    || name.contains(">") || name.contains("<") || name.contains("|")
              //  || name.contains(" ") || name.contains("\t") || name.contains("\n")) {
            throw new IllegalArgumentException("TableProvider.removeTable: bad table name \"" + name + "\"");
        }
        if (tables.containsKey(name)) {
            try {
                Shell.cd(dbPath.toString());
                Shell.rm(name);
                tables.remove(name);
            } catch (IOException e) {
                throw new IOException("TableProvider.removeTable: directory removing error with message \""
                        + e.getMessage() + "\"");
            }
        } else {
            throw new IllegalStateException("TableProvider.removeTable: table doesn't exists");
        }
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
    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (table == null) {
            throw new IllegalArgumentException("TableProvider.deserialize: table is null");
        }
        if ((value == null) || (value.isEmpty()) || (value.indexOf('\n') != -1)) {
            throw new IllegalArgumentException("TableProvider.deserialize: bad value");
        }
        Storeable s = createFor(table);
        StringTokenizer tokenParser = new StringTokenizer(value, "<>");
        int i = 0;
        int pos = 0;
        boolean rowInside = false;
        boolean colInside = false;
        while (tokenParser.hasMoreTokens()) {
            String token = tokenParser.nextToken();
            switch (token) {
                case "row":
                    if (rowInside) {
                        throw new ParseException("TableProvider.deserialize: incorrect order of tags", pos);
                    }
                    rowInside = true;
                    pos += 5;
                    break;
                case "/row":
                    if (!rowInside) {
                        throw new ParseException("TableProvider.deserialize: incorrect order of tags", pos);
                    }
                    rowInside = false;
                    pos += 6;
                    break;
                case "col":
                    if ((!rowInside) || (colInside)) {
                        throw new ParseException("TableProvider.deserialize: incorrect order of tags", pos);
                    }
                    colInside = true;
                    pos += 5;
                    break;
                case "/col":
                    if ((!rowInside) || (!colInside)) {
                        throw new ParseException("TableProvider.deserialize: incorrect order of tags", pos);
                    }
                    colInside = false;
                    pos += 6;
                    break;
                case "null/":
                    if ((!rowInside) || (colInside)) {
                        throw new ParseException("TableProvider.deserialize: incorrect order of tags", pos);
                    }
                    s.setColumnAt(i, null);
                    i++;
                    pos += 7;
                    break;
                default:
                    if ((!rowInside) || (!colInside)) {
                        throw new ParseException("TableProvider.deserialize: incorrect order of tags", pos);
                    }
                    try {
                        Class<?> c = table.getColumnType(i);
                        if (c == Integer.class) {
                            s.setColumnAt(i, Integer.parseInt(token));
                        } else if (c == Long.class)  {
                            s.setColumnAt(i, Long.parseLong(token));
                        } else if (c == Byte.class) {
                            s.setColumnAt(i, Byte.parseByte(token));
                        } else if (c == Float.class) {
                            s.setColumnAt(i, Float.parseFloat(token));
                        } else if (c == Double.class) {
                            s.setColumnAt(i, Double.parseDouble(token));
                        } else if (c == Boolean.class) {
                            s.setColumnAt(i, Boolean.parseBoolean(token));
                        } else {
                            s.setColumnAt(i, token);
                        }
                        i++;
                        pos += token.length();
                    } catch (ColumnFormatException e) {
                        throw new ParseException("TableProvider.deserialize: " + e.getMessage(), pos);
                    }
            }
        }
        return s;
    }

    /**
     * Преобразовывает объект {@link ru.fizteh.fivt.storage.structured.Storeable} в строку.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @param value {@link ru.fizteh.fivt.storage.structured.Storeable}, который нужно записать.
     * @return Строка с записанным значением.
     *
     * @throws ru.fizteh.fivt.storage.structured.ColumnFormatException При несоответствии типа в
     *              {@link ru.fizteh.fivt.storage.structured.Storeable} и типа колонки в таблице.
     */
    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        try {
            value.getColumnAt(table.getColumnsCount());
            throw new ColumnFormatException("Table.put: value has other number of columns");
        } catch (IndexOutOfBoundsException e) {
            try {                                                                    //!!!!ЗДЕСЬ ОГРОМНЕЙШИЙ КОСТЫЛЬ!!!!
                Storeable temp = value;
                for (int i = 0; i < table.getColumnsCount(); ++i) {
                    Class<?> c = table.getColumnType(i);
                    if (c == Integer.class) {
                        //int tempValue = value.getIntAt(i);
                        temp.setColumnAt(i, Integer.valueOf(1));
                        //temp.setColumnAt(i, tempValue);
                    } else if (c == Long.class)  {
                        //long tempValue = value.getLongAt(i);
                        temp.setColumnAt(i, Long.valueOf((long) 1));
                        //temp.setColumnAt(i, tempValue);
                    } else if (c == Byte.class) {
                        //byte tempValue = value.getByteAt(i);
                        temp.setColumnAt(i, Byte.valueOf((byte) 1));
                        //temp.setColumnAt(i, tempValue);
                    } else if (c == Float.class) {
                        //float tempValue = value.getFloatAt(i);
                        temp.setColumnAt(i, Float.valueOf((float) 1.5));
                        //temp.setColumnAt(i, tempValue);
                    } else if (c == Double.class) {
                        //double tempValue = value.getDoubleAt(i);
                        temp.setColumnAt(i, Double.valueOf(1.5));
                        //temp.setColumnAt(i, tempValue);
                    } else if (c == Boolean.class) {
                        //boolean tempValue = value.getBooleanAt(i);
                        temp.setColumnAt(i, Boolean.valueOf(true));
                        //temp.setColumnAt(i, tempValue);
                    } else {
                        //String tempValue = value.getStringAt(i);
                        temp.setColumnAt(i, "abc");
                        //temp.setColumnAt(i, tempValue);
                    }
                }
            } catch (IndexOutOfBoundsException e1) {
                throw new ColumnFormatException("Table.put: value has other number of columns");
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<row>");
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            Class<?> c = table.getColumnType(i);
            if (c == null) {
                sb.append("<null/>");
            } else {
                sb.append("<col>");
                if (c == Integer.class) {
                    sb.append(value.getIntAt(i).toString());
                } else if (c == Long.class)  {
                    sb.append(value.getLongAt(i).toString());
                } else if (c == Byte.class) {
                    sb.append(value.getByteAt(i).toString());
                } else if (c == Float.class) {
                    sb.append(value.getFloatAt(i).toString());
                } else if (c == Double.class) {
                    sb.append(value.getDoubleAt(i).toString());
                } else if (c == Boolean.class) {
                    sb.append(value.getBooleanAt(i).toString());
                } else {
                    sb.append(value.getStringAt(i));
                }
                sb.append("</col>");
            }
        }
        sb.append("</row>");
        return sb.toString();
    }

    /**
     * Создает новый пустой {@link ru.fizteh.fivt.storage.structured.Storeable} для указанной таблицы.
     *
     * @param table Таблица, которой должен принадлежать {@link ru.fizteh.fivt.storage.structured.Storeable}.
     * @return Пустой {@link ru.fizteh.fivt.storage.structured.Storeable}, нацеленный на использование с этой таблицей.
     */
    @Override
    public Storeable createFor(Table table) {
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            columnTypes.add(table.getColumnType(i));
        }
        try {
            return (new Storable(columnTypes));
        } catch (ColumnFormatException e) {
            return null;
        }
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
    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            columnTypes.add(table.getColumnType(i));
        }
        return (new Storable(columnTypes, (new ArrayList<>(values))));
    }

    /**
     * Проверяет базу данных на правильность ее структуры, т.е. правильность расположения и названий файлов и папок
     *
     * @throws IOException При нахождении ошибок в структуре базы данных
     */
    void checkDataBaseDirectory() throws IOException {
        if (!Files.exists(dbPath)) {
            throw new IOException("Directory \"" + dbPath.toString() + "\" doesn't exists");
        }
        if (!Files.isDirectory(dbPath)) {
            throw new IOException("\"" + dbPath.toString() + "\" isn't a directory");
        }
        Pattern levelPattern = Pattern.compile("([0-9]|1[0-5])\\.dir");
        Pattern partPattern = Pattern.compile("([0-9]|1[0-5])\\.dat");

        DirectoryStream<Path> tables = Files.newDirectoryStream(dbPath);
        for (Path table : tables) {
            if (!Files.isDirectory(table)) {
                throw new IOException("there is object which is not a directory in root directory");
            }
            DirectoryStream<Path> levels = Files.newDirectoryStream(table);
            for (Path level : levels) {
                if (!level.getFileName().toString().equals("signature.tsv")) {
                    if (!Files.isDirectory(level)) {
                        throw new IOException("there is object which is not a directory in table \""
                                + table.getFileName() + "\"");
                    }
                    if (!levelPattern.matcher(level.getFileName().toString()).matches()) {
                        throw new IOException("there is directory with wrong name in table \""
                                + table.getFileName() + "\"");
                    }
                    DirectoryStream<Path> parts = Files.newDirectoryStream(level);
                    for (Path part : parts) {
                        if (Files.isDirectory(part)) {
                            throw new IOException("there is object which is not a file in \""
                                    + table.getFileName() + "\\" + level.getFileName() + "\"");
                        }
                        if (!partPattern.matcher(part.getFileName().toString()).matches()) {
                            throw new IOException("there is file with wrong name in \""
                                    + table.getFileName() + "\\" + level.getFileName() + "\"");
                        }
                    }
                }
            }
        }
    }
}
