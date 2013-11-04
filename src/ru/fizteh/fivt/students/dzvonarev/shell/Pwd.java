package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.IOException;
import java.util.Vector;

public class Pwd implements CommandInterface {

    public void execute(Vector<String> args) throws IOException {
        String expr = args.elementAt(0);
        if (!expr.equals("pwd")) {
            throw new IOException("pwd: wrong parameters");
        }
        System.out.println(Shell.getCurrentDirectory());
    }

}
