package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.FileManager;

import java.io.File;
import java.io.IOException;

public class TableManager extends FileManager {

    protected BasicTable currentTable;

    protected TableManager() {
    }

    public TableManager(File tableDirectory, String tableName) throws IOException {
        currentTable = new BasicTable(tableDirectory, tableName);
    }

    @Override
    public void setExit() {
        if (currentTable != null) {
            currentTable.commit();
        }
        super.setExit();
    }

    public Table getCurrentTable() {
        return currentTable;
    }
}
