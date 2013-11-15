package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.File;
import java.io.IOException;

public class CommandDrop implements Command<MultiFileHashMapState> {
    public String getName() {
        return "drop";
    }

    public int getArgCount() {
        return 1;
    }

    public void run(MultiFileHashMapState state, String[] args) throws IOException {
        File db = state.getPath().resolve(args[0]).toFile();
        if (!db.exists()) {
            System.out.println(args[0] + " not exists");
        } else {
            if (db.listFiles() != null) {
                for (File dbDir : db.listFiles()) {
                    if (dbDir.listFiles() != null) {
                        for (File entry : dbDir.listFiles()) {
                            entry.delete();
                        }
                    }
                    dbDir.delete();
                }
            }
            if (args[0].equals(state.getWorkingTableName())) {
                state.getCurrentTable().clear();
                state.setCurrentTable("");
                state.setWorkingPath("");
            }
            db.delete();
            state.deleteTable(args[0]);
            System.out.println("dropped");
        }
    }
}