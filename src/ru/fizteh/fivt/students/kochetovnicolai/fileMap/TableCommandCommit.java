package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

public class TableCommandCommit extends Executable {
    TableManager manager;

    @Override
    public boolean execute(String[] args) {
        Table table = manager.getCurrentTable();
        if (table == null) {
            manager.printMessage("no table");
            return false;
        }
        manager.printMessage(Integer.toString(table.commit()));
        return true;
    }

    public TableCommandCommit(TableManager tableManager) {
        super("commit", 1);
        manager = tableManager;
    }
}
