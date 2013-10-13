package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;

public class Dir {

    public static void listingCurrDir() {
        File dir = new File(Main.getCurrentDirectory());
        String[] path = dir.list();
        for (String file : path) {
            File currFile = new File(file);
            if (!currFile.isHidden()) {
                System.out.println(file);
            }
        }
    }

}
