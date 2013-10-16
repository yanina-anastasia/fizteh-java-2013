package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        String dbAddress = System.getProperty("fizteh.db.dir");
        MapBuilder mb = new MapBuilder();
        try {
            Path db = new File(dbAddress).getCanonicalFile().toPath().resolve("db.dat");
            State state = new State(db);
            mb.buildMap(state);
            Shell shell = new Shell(state);
            if (args.length == 0) {
                shell.interactiveMode();
            } else {
                shell.batchMode(args);
            }
            mb.fillFile(state);
        } catch (IOException e) {
            System.err.println("File not found.\n");
            System.exit(1);
        }
        System.exit(0);
    }
}