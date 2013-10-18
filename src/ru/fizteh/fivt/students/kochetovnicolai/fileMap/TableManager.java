package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Manager;

import java.io.File;
import java.io.IOException;

public class TableManager implements Manager {

    BasicTable currentTable;

    public TableManager(File tableDirectory, String tableName) throws IOException {
        currentTable = new BasicTable(tableDirectory, tableName);
    }

    @Override
    public boolean timeToExit() {
        return currentTable.timeToExit();
    }

    @Override
    public void printMessage(final String message) {
        currentTable.printMessage(message);
    }

    @Override
    public void printSuggestMessage() {
        currentTable.printSuggestMessage();
    }

    @Override
    public void setExit() {
        currentTable.commit();
        currentTable.setExit();
    }

    public Table getCurrentTable() {
        return currentTable;
    }
}
