package ru.fizteh.fivt.students.kislenko.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

public class MyTableProviderFactory implements TableProviderFactory {
    @Override
    public MyTableProvider create(String path) throws IOException {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Incorrect database name.");
        }
        return new MyTableProvider();
    }
}
