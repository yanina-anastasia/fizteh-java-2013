package ru.fizteh.fivt.students.dzvonarev.storeable;


import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.util.ArrayList;

public class DataBaseSize implements CommandInterface {

    public DataBaseSize(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(ArrayList<String> args) throws IOException, IllegalArgumentException {
        String tableName = tableProvider.getCurrentTable();
        if (tableName == null) {
            throw new IOException("no table");
        }
        int size = tableProvider.getSize();
        System.out.println(size);
    }

}
