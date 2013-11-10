package ru.fizteh.fivt.students.dzvonarev.filemap;

import java.io.File;
import java.io.IOException;

public class ShellRemove {

    private static void remove(File aim) throws IOException {
        if (aim.isDirectory()) {
            if (aim.list().length == 0) {
                if (!aim.delete()) {
                    throw new IOException("drop: can't remove " + aim + " : no such file or directory");
                }
            } else {
                String[] file = aim.list();
                for (String aFile : file) {
                    File currFile = new File(aim, aFile);
                    remove(currFile);
                }
                if (aim.list().length == 0) {
                    if (!aim.delete()) {
                        throw new IOException("drop: can't remove " + aim + " : no such file or directory");
                    }
                }
            }
        } else {
            if (!aim.delete()) {
                throw new IOException("drop: can't remove " + aim + " : no such file or directory");
            }
        }
    }

    public static void execute(String expr) throws IOException {
        String path = System.getProperty("fizteh.db.dir") + File.separator + expr;
        if ((new File(path)).isFile() || (new File(path)).isDirectory()) {
            remove(new File(path));
        } else {
            throw new IOException("drop: can't remove " + path);
        }
    }

}
