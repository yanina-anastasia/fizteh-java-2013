package ru.fizteh.fivt.students.valentinbarishev.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class Context {
    public TableProvider provider;
    public Table table;

    public Context(TableProvider newProvider) {
        provider = newProvider;
        table = null;
    }
}
