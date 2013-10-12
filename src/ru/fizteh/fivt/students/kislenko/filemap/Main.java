package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String dbAddress = System.getProperty("fizteh.db.dir");
        File db = new File(dbAddress);
        try {
            db = db.getCanonicalFile().toPath().resolve("db.dat").toFile();
        } catch (IOException e) {
            System.err.println("File not found.\n");
            System.exit(1);
        }
        State state = new State();
        state.setState(db.toPath());
        Shell shell = new Shell(state);
        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            shell.batchMode(args);
        }
        System.exit(0);
    }
}