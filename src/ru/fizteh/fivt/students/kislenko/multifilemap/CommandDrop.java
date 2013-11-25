package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.File;

public class CommandDrop implements Command<MultiTableFatherState> {
    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public int getArgCount() {
        return 1;
    }

    @Override
    public void run(MultiTableFatherState state, String[] args) throws Exception {
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
            state.deleteTable(args[0]);
            db.delete();
            System.out.println("dropped");
        }
    }
}
