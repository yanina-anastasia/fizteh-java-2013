package ru.fizteh.fivt.students.dzvonarev.storeable;

import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.util.ArrayList;

public class DataBaseRollback implements CommandInterface {

    public DataBaseRollback(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(ArrayList<String> args) throws IOException, IllegalArgumentException {
        String tableName = tableProvider.getCurrentTable();
        if (tableName == null) {
            throw new IOException("no table");
        }
        MyTable currTable = tableProvider.getTable(tableName);
        int changes = currTable.rollback();
        System.out.println(changes);
    }
}
