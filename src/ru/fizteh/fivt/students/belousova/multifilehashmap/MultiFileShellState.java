package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.belousova.filemap.TableState;

public class MultiFileShellState extends TableState{
    private TableProvider tableProvider = null;

    public MultiFileShellState(TableProvider tableProvider, Table table) {
        this.tableProvider = tableProvider;
        this.currentTable = table;
    }

    public Table getTable(String name) {
        return tableProvider.getTable(name);
    }

    public Table createTable(String name) {
        return tableProvider.createTable(name);
    }

    public void removeTable(String name) {
        tableProvider.removeTable(name);
    }

    public void setCurrentTable(String name) {
        currentTable = tableProvider.getTable(name);
    }

    public void resetCurrentTable() {
        currentTable = null;
    }
}
