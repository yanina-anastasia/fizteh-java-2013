package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class Create implements CommandInterface {

    public void execute(Vector<String> args) throws IOException {
        String str = args.elementAt(0);
        int spaceIndex = str.indexOf(' ', 0);
        if (spaceIndex == -1) {
            throw new IOException("create: wrong parameters");
        }
        while (str.indexOf(' ', spaceIndex + 1) == spaceIndex + 1) {
            ++spaceIndex;
        }
        if (str.indexOf(' ', spaceIndex + 1) != -1) {
            throw new IOException("create: wrong parameters");
        }
        String newName = str.substring(spaceIndex + 1, str.length());
        File newTable = new File(System.getProperty("fizteh.db.dir") + File.separator + newName);
        if (newTable.exists()) {
            if (newTable.isFile()) {
                throw new IOException("create: such file = " + newName + " exists");
            }
            if (newTable.isDirectory()) {
                System.out.println(newName + " exists");
            }
        } else {
            if (!(new File(System.getProperty("fizteh.db.dir") + File.separator + newName)).mkdir()) {
                throw new IOException("create: can't create table");
            }
            HashMap<String, String> newMap = new HashMap<>();
            HashMap<String, HashMap<String, String>> myMultiMap = MultiFileMap.getMultiFileMap();
            myMultiMap.put(newName, newMap);
            System.out.println("created");
        }
    }

}
