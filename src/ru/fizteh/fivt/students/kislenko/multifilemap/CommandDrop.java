package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class CommandDrop implements Command<MultiFileHashMapState> {
    public String getName() {
        return "drop";
    }

    public int getArgCount() {
        return 1;
    }

    public void run(MultiFileHashMapState state, String[] args) throws IOException {
        File db = state.getPath().resolve(args[0]).toFile();
        if (!args[0].matches("([0-9]|1[0-5]).dir")) {
            throw new IOException("Incorrect table name");
        }
        if (!db.exists()) {
            System.out.println(args[0] + " not exists");
        } else {
            if (db.listFiles() != null) {
                for (File entry : db.listFiles()) {
                    entry.delete();
                }
            }
            if (args[0].equals(state.getWorkingTableName())) {
                state.getMap().clear();
                state.setWorkingPath("");
            }
            db.delete();
            state.deleteTable(args[0]);
            System.out.println("dropped");
        }
    }
}