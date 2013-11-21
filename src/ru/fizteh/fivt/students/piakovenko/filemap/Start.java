package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.students.piakovenko.shell.Shell;

import java.io.File;

public class Start {
    public static void main(String[] args) {
        File fileMapStorage = new File(System.getProperty("fizteh.db.dir"));
        Shell shell = new Shell();
        DataBase db = new DataBase(shell, fileMapStorage);
        db.initialize();
        shell.start(args);
    }
}
