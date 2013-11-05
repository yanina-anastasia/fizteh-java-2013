package ru.fizteh.fivt.students.nlevashov.factory;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.nlevashov.shell.Shell;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.io.IOException;

public class MyTableProvider implements TableProvider {

    HashMap<String, Table> tables;
    Path dbPath;

    /**
     * Конструктор. Составляет список таблиц в базе
     *
     * @param path Адрес базы данных.
     */
    public MyTableProvider(Path path) {
        tables = new HashMap<String, Table>();
        dbPath = path;
        String name = path.getFileName().toString();
        if ((name == null) || name.trim().isEmpty()
                || name.contains("/") || name.contains(":") || name.contains("*")
                || name.contains("?") || name.contains("\"") || name.contains("\\")
                || name.contains(">") || name.contains("<") || name.contains("|")
                || name.contains(" ") || name.contains("\\t") || name.contains("\\n")) {
            throw new IllegalArgumentException("TableProvider.constructor: bad table name \"" + name + "\"");
        }
        if (!Files.exists(dbPath)) {
            try {
                if (!path.toFile().getCanonicalFile().mkdir()) {
                    throw new IOException("Directory \"" + path.getFileName() + "\" wasn't created");
                }
            } catch (IOException e) {
                throw new RuntimeException("TableProvider: " + e.getMessage());
            }
        } else if (!Files.isDirectory(dbPath)) {
            throw new IllegalArgumentException("TableProvider.constructor: object with said path isn't a directory");
        }
        try {
            checkDataBaseDirectory();
            DirectoryStream<Path> stream = Files.newDirectoryStream(path);
            for (Path f : stream) {
                tables.put(f.getFileName().toString(), (new MyTable(f)));
            }
        } catch (IOException e) {
            throw new RuntimeException("TableProvider.constructor: the structure of database is wrong. Specifically, "
                                        + e.getMessage());
        }
    }

    /**
     * Возвращает таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблицы с указанным именем не существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    @Override
    public Table getTable(String name) {
        if ((name == null) || name.trim().isEmpty()
            || name.contains("/") || name.contains(":") || name.contains("*")
            || name.contains("?") || name.contains("\"") || name.contains("\\")
            || name.contains(">") || name.contains("<") || name.contains("|")
            || name.contains(" ") || name.contains("\\t") || name.contains("\\n")) {
            throw new IllegalArgumentException("TableProvider.getTable: bad table name \"" + name + "\"");
        }
        return tables.get(name);
    }

    /**
     * Создаёт таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблица уже существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    @Override
    public Table createTable(String name) {
        if ((name == null) || name.trim().isEmpty()
                || name.contains("/") || name.contains(":") || name.contains("*")
                || name.contains("?") || name.contains("\"") || name.contains("\\")
                || name.contains(">") || name.contains("<") || name.contains("|")
                || name.contains(" ") || name.contains("\\t") || name.contains("\\n")) {
            throw new IllegalArgumentException("TableProvider.getTable: bad table name \"" + name + "\"");
        }
        if (tables.containsKey(name)) {
            return null;
        } else {
            try {
                Shell.cd(dbPath.toString());
                Shell.mkdir(name);
            } catch (Exception e) {
                throw new RuntimeException("TableProvider.createTable: directory making error with message \""
                                            + e.getMessage() + "\"");
            }
            Table newTable = new MyTable(dbPath.resolve(name));
            tables.put(name, newTable);
            return newTable;
        }
    }

    /**
     * Удаляет таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException    Если таблицы с указанным названием не существует.
     */
    @Override
    public void removeTable(String name) {
        if ((name == null) || name.trim().isEmpty()
                || name.contains("/") || name.contains(":") || name.contains("*")
                || name.contains("?") || name.contains("\"") || name.contains("\\")
                || name.contains(">") || name.contains("<") || name.contains("|")
                || name.contains(" ") || name.contains("\\t") || name.contains("\\n")) {
            throw new IllegalArgumentException("TableProvider.removeTable: bad table name \"" + name + "\"");
        }
        if (tables.containsKey(name)) {
            try {
                Shell.cd(dbPath.toString());
                Shell.rm(name);
                tables.remove(name);
            } catch (Exception e) {
                throw new RuntimeException("TableProvider.removeTable: directory removing error with message \""
                                            + e.getMessage() + "\"");
            }
        } else {
            throw new IllegalStateException("TableProvider.removeTable: table doesn't exists");
        }
    }

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
