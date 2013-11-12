package ru.fizteh.fivt.students.chernigovsky.filemap;

import ru.fizteh.fivt.students.chernigovsky.junit.ExtendedMultiFileHashMapTable;
import ru.fizteh.fivt.students.chernigovsky.junit.ExtendedMultiFileHashMapTableProvider;

public class State {
    private ExtendedMultiFileHashMapTable currentTable;
    private ExtendedMultiFileHashMapTableProvider currentTableProvider;

    public State(ExtendedMultiFileHashMapTable newTable, ExtendedMultiFileHashMapTableProvider newTableProvider) {
        currentTable = newTable;
        currentTableProvider = newTableProvider;
    }

    public ExtendedMultiFileHashMapTable getCurrentTable() {
        return currentTable;
    }

    public void changeCurrentTable(ExtendedMultiFileHashMapTable newCurrentTable) {
        currentTable = newCurrentTable;
    }

    public ExtendedMultiFileHashMapTableProvider getCurrentTableProvider() {
        return currentTableProvider;
    }

}
