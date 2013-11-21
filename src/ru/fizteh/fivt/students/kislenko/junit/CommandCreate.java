package ru.fizteh.fivt.students.kislenko.junit;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.File;
import java.io.IOException;

public class CommandCreate implements Command<MultiFileHashMapState> {
    public String getName() {
        return "create";
    }

    public int getArgCount() {
        return 1;
    }

    public void run(MultiFileHashMapState state, String[] args) throws IOException {
        File db = state.getPath().resolve(args[0]).toFile();
        if (db.exists()) {
            System.out.println(args[0] + " exists");
        } else {
            state.createTable(args[0]);
            db.mkdir();
            System.out.println("created");
        }
    }
}