package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class Exit implements CommandInterface {

    public void execute(Vector<String> args) throws IOException {
        HashMap<String, HashMap<String, String>> myMap = MultiFileMap.getMultiFileMap();
        File dir = new File(System.getProperty("fizteh.db.dir"));
        String[] file = dir.list();
        if (file != null) {
            if (file.length != 0) {
                for (String currFile : file) {
                    if (new File(System.getProperty("fizteh.db.dir") + File.separator + currFile).isFile()) {
                        continue;
                    }
                    if (new File(System.getProperty("fizteh.db.dir") + File.separator + currFile).isDirectory() &&
                            !(new File(System.getProperty("fizteh.db.dir") + File.separator + currFile)).isHidden()) {
                        MultiFileMap.writeMap(myMap, currFile);
                    }
                }
            }
        }
        System.exit(0);
    }

}
