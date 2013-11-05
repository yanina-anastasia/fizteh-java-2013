package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class Drop implements CommandInterface {

    public void execute(Vector<String> args) throws IOException {
        String str = args.elementAt(0);
        int spaceIndex = str.indexOf(' ', 0);
        if (spaceIndex == -1) {
            throw new IOException("drop: wrong parameters");
        }
        while (str.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
            ++spaceIndex;
        }
        if (str.indexOf(' ', spaceIndex + 1) != -1) {
            throw new IOException("drop: wrong parameters");
        }
        String table = str.substring(spaceIndex + 1, str.length());
        if (new File(System.getProperty("fizteh.db.dir") + File.separator + table).exists()) {
            if (new File(System.getProperty("fizteh.db.dir") + File.separator + table).isDirectory()) {
                HashMap<String, HashMap<String, String>> myMultiMap = MultiFileMap.getMultiFileMap();
                myMultiMap.remove(table);
                ShellRemove.execute(table);
                System.out.println("dropped");
                if (table.equals(MultiFileMap.getWorkingTable())) {
                    MultiFileMap.changeWorkingTable("noTable");
                }
            } else {
                System.out.println(table + " not exists");
            }
        } else {
            System.out.println(table + " not exists");
        }
    }

}
