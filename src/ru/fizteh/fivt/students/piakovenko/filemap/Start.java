package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.students.piakovenko.shell.Shell;

public class Start {
    public static void main(String[] args) {
        Shell shell = new Shell();
        DataBase db = new DataBase(shell);
        db.initialize();
        shell.start(args);
    }
}
