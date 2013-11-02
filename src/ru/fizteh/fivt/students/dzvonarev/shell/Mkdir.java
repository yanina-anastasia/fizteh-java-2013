package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class Mkdir implements CommandInterface {

    public void execute(Vector<String> args) throws IOException {
        String expr = args.elementAt(0);
        if (expr.equals("mkdir")) {
            throw new IOException("mkdir: wrong  parameters");
        }
        int spaceIndex = expr.indexOf(' ', 0);
        int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
        if (newSpaceIndex != -1) {
            throw new IOException("mkdir: wrong parameters");
        }
        String folder = Shell.getAbsPath(expr.substring(spaceIndex + 1, expr.length()));
        File dir = new File(folder);
        if (!dir.mkdir()) {
            throw new IOException("mkdir: can't create " + expr.substring(spaceIndex + 1, expr.length()) + " folder");
        }
    }

}
