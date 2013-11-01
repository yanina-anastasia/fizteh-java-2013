package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public abstract class AbstractTableProvider implements TableProvider {

    public DatabaseState state;

    public static final String UNSUPPORTED_TABLE_NAME = "Unsupported table name";
    public static final String TABLE_NOT_EXIST = "Table not exist";
    public static final int MAX_SUPPORTED_NAME_LENGTH = 1024;

    protected void validTableNameCheck (String tableName) throws IllegalArgumentException {
        if ((tableName == null) || (tableName.length () > MAX_SUPPORTED_NAME_LENGTH)) {
            throw new IllegalArgumentException (UNSUPPORTED_TABLE_NAME);
        }
        if (!Pattern.matches ("[a-zA-Zа-яА-Я0-9]+",tableName)) {
            throw new IllegalArgumentException (UNSUPPORTED_TABLE_NAME);
        }
        if (tableName.trim ().isEmpty ()) {
            throw new IllegalArgumentException (UNSUPPORTED_TABLE_NAME);
        }

    }

    public AbstractTableProvider (DatabaseState state) throws IOException {
        this.state = state;
    }

    protected void isDirectoryCheck (Path path) throws IllegalArgumentException {
        if (!Files.isDirectory (path)) {
            throw new IllegalArgumentException ("[" + path + "] expected to be a directory");
        }
    }

    protected void isFileCheck (Path path) throws IllegalArgumentException {
        if (Files.isDirectory (path)) {
            throw new IllegalArgumentException ("[" + path + "] expected not to be a directory");
        }
    }

    protected void existsCheck (Path path) throws IllegalArgumentException {
        if (Files.notExists (path)) {
            throw new IllegalArgumentException ("Expected file [" + path + "] but it suddenly does not exist");
        }
    }

    protected abstract void getDatabaseFromDisk () throws IOException, IllegalArgumentException;
}
