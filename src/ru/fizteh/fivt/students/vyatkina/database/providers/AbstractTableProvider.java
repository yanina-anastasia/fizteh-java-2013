package ru.fizteh.fivt.students.vyatkina.database.providers;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.vyatkina.database.DatabaseState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractTableProvider implements TableProvider {

    public DatabaseState state;

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
