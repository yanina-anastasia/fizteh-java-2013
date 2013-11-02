package ru.fizteh.fivt.students.dzvonarev.multifilemap;

import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class Use implements CommandInterface {

    public void execute(Vector<String> args) throws IOException {
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
        String path = System.getProperty("fizteh.db.dir") + File.separator + tableName;
        if ((new File(path)).exists() && (new File(path)).isDirectory()) {
            System.out.println("using " + tableName);
            MultiFileMap.changeWorkingTable(tableName);
        } else {
            throw new IOException(tableName + " not exists");
        }
    }

}


