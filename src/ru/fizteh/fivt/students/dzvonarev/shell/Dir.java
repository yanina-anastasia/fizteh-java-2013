package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.util.Vector;

public class Dir implements CommandInterface {

    public void execute(Vector<String> args) {
        File dir = new File(Shell.getCurrentDirectory());
        String[] path = dir.list();
        for (String file : path) {
            File currFile = new File(file);
            if (!currFile.isHidden()) {
                System.out.println(file);
            }
        }
    }

}
