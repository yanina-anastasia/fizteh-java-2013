package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory {
    @Override
    public MyTableProvider create(String dir) {
        if (dir == null || dir.equals("")) {
            throw new IllegalArgumentException("Incorrect database name.");
        }
        MyTableProvider provider = new MyTableProvider();
        provider.createTable("");
        return provider;
    }
}