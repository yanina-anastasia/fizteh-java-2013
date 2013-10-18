package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;

public class Cd {

    public static void changeDir(String expr, int spaceIndex) throws IOException {
        if (expr.indexOf(' ', spaceIndex + 1) != -1) {
            throw new IOException("cd: wrong parameters");
        }
        String destination = DoCommand.getAbsPath(expr.substring(spaceIndex + 1, expr.length()));
        File destFile = new File(destination);
        if (destFile.isDirectory()) {
            Main.changeCurrentDirectory(destination);
        } else {
            throw new IOException("cd: can't change directory to " + expr.substring(spaceIndex + 1, expr.length()));
        }
    }

}
