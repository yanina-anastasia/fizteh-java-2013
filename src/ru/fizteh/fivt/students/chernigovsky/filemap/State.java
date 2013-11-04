package ru.fizteh.fivt.students.chernigovsky.filemap;

import ru.fizteh.fivt.students.chernigovsky.junit.MyTable;
import ru.fizteh.fivt.students.chernigovsky.junit.MyTableProvider;

public class State {
    private MyTable currentTable;
    private MyTableProvider currentTableProvider;

    public State(MyTable newTable, MyTableProvider newTableProvider) {
        currentTable = newTable;
        currentTableProvider = newTableProvider;
    }

    public MyTable getCurrentTable() {
        return currentTable;
    }

    public MyTableProvider getCurrentTableProvider() {
        return currentTableProvider;
    }
}
