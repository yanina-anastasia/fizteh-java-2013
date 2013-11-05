package ru.fizteh.fivt.students.kamilTalipov.database;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.FileNotFoundException;

public class MultiFileHashTableFactory implements TableProviderFactory {
    @Override
    public MultiFileHashTableProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("Directory path must be not null");
        }

        try {
            return new MultiFileHashTableProvider(dir);
        } catch (DatabaseException e) {
            IllegalArgumentException exception = new IllegalArgumentException("Database error");
            exception.addSuppressed(e);
            throw exception;
        } catch (FileNotFoundException e) {
            IllegalArgumentException exception = new IllegalArgumentException("File not found");
            exception.addSuppressed(e);
            throw exception;
        }
    }
}
