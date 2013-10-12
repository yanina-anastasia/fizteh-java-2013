package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;

public class Mkdir {

    public static void makeDir(String expr, int spaceIndex) throws IOException {
        int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
        if (newSpaceIndex != -1) {
            throw new IOException("Wrong parametres of mkdir");
        }
        String folder = DoCommand.getAbsPath(expr.substring(spaceIndex + 1, expr.length()));
        File dir = new File(folder);
        if (!dir.mkdir()) {
            throw new IOException("mkdir: can't create " + expr.substring(spaceIndex + 1, expr.length()) + " folder");
        }
    }

}
