package ru.fizteh.fivt.students.kamilTalipov.database.core;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MultiFileHashTableFactory implements TableProviderFactory {
    @Override
    public MultiFileHashTableProvider create(String dir) throws IllegalArgumentException, IOException {
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
        } /*catch (IllegalArgumentException e) {
            IOException exception = new IOException("Provider io error");
            exception.addSuppressed(e);
            throw exception;
        }*/
    }
}
