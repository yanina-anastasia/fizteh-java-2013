package ru.fizteh.fivt.students.dzvonarev.shell;

import java.util.Vector;

public class Pwd implements CommandInterface {

    public void execute(Vector<String> args) {
        System.out.println(Shell.getCurrentDirectory());
    }

}
