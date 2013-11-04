package ru.fizteh.fivt.students.chernigovsky.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.chernigovsky.junit.ExtendedTable;
import ru.fizteh.fivt.students.chernigovsky.junit.ExtendedTableProvider;
import ru.fizteh.fivt.students.chernigovsky.junit.MyTable;
import ru.fizteh.fivt.students.chernigovsky.junit.MyTableProvider;

public class State {
    private ExtendedTable currentTable;
    private ExtendedTableProvider currentTableProvider;

    public State(ExtendedTable newTable, ExtendedTableProvider newTableProvider) {
        currentTable = newTable;
        currentTableProvider = newTableProvider;
    }

    public ExtendedTable getCurrentTable() {
        return currentTable;
    }

    public void changeCurrentTable(ExtendedTable newCurrentTable) {
        currentTable = newCurrentTable;
    }

    public ExtendedTableProvider getCurrentTableProvider() {
        return currentTableProvider;
    }

}
