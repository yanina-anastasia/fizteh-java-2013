package ru.fizteh.fivt.students.eltyshev.storable;

import ru.fizteh.fivt.storage.structured.*;

public class StoreableShellState {
    public TableProvider provider;
    public Table table;

    public StoreableShellState(TableProvider provider) {
        this.provider = provider;
        this.table = null;
    }
}
