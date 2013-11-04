package ru.fizteh.fivt.students.dzvonarev.filemap;


import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.util.Vector;

public class DataBaseUse implements CommandInterface {

    public DataBaseUse(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(Vector<String> args) throws IOException, IllegalArgumentException {
        String str = args.elementAt(0);
        int spaceIndex = str.indexOf(' ', 0);
        if (spaceIndex == -1) {
            throw new IOException("use: wrong parameters");
        }
        while (str.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
            ++spaceIndex;
        }
        if (str.indexOf(' ', spaceIndex + 1) != -1) {
            throw new IOException("use: wrong parameters");
        }
        String tableName = str.substring(spaceIndex + 1, str.length());
        if (tableProvider.getCurrentTable() != null) {
            MyTable currTable = tableProvider.getTable(tableProvider.getCurrentTable());
            System.out.println(currTable.getCountOfChanges() + " unsaved changes");
        }
        if (tableProvider.changeCurrentTable(tableName) == -1) {
            throw new IOException(tableName + " not exists");
        } else {
            System.out.println("using " + tableName);
        }
    }

}
