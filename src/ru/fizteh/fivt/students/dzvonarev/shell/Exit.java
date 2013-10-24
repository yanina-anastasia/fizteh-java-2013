package ru.fizteh.fivt.students.dzvonarev.shell;

import java.util.Vector;

public class Exit implements CommandInterface {

    public void execute(Vector<String> args) {
        System.exit(0);
    }

}
