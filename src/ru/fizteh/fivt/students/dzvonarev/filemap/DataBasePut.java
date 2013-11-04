package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.IOException;
import java.util.Vector;

public class DataBasePut implements CommandInterface {

    public DataBasePut(MyTableProvider newTableProvider) {
        tableProvider = newTableProvider;
    }

    private MyTableProvider tableProvider;

    public void execute(Vector<String> args) throws IOException, IllegalArgumentException {
        String tableName = tableProvider.getCurrentTable();
        if (tableName == null) {
            throw new IOException("no table");
        }
        String str = args.elementAt(0);
        int spaceIndex = str.indexOf(' ', 0);
        while (str.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
            ++spaceIndex;
        }
        int newSpaceIndex = str.indexOf(' ', spaceIndex + 1);
        if (newSpaceIndex == -1) {
            throw new IOException("put: wrong parameters");
        }
        int index = newSpaceIndex;
        String key = str.substring(spaceIndex + 1, index);
        while (str.indexOf(' ', newSpaceIndex + 1) == newSpaceIndex + 1) {
            ++newSpaceIndex;
        }
        String value = str.substring(newSpaceIndex + 1, str.length());
        MyTable currTable = tableProvider.getTable(tableName);
        String result = currTable.put(key, value);
        if (result != null) {
            System.out.println("overwrite");
            System.out.println(result);
        } else {
            System.out.println("new");
        }
    }

}
