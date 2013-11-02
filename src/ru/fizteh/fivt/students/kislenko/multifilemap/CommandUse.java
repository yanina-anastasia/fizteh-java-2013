package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.File;
import java.io.IOException;

public class CommandUse implements Command<MultiFileHashMapState> {
    public String getName() {
        return "use";
    }

    public int getArgCount() {
        return 1;
    }

    public void run(MultiFileHashMapState state, String[] args) throws IOException {
        if (state.getWorkingPath().getFileName().toString().equals(args[0])) {
            System.out.println("using " + args[0]);
            return;
        }
        MyTable table = state.getCurrentTable();
        if (table != null && table.getChangeCount() != 0) {
            System.out.println(state.getCurrentTable().getChangeCount() + " unsaved changes");
            throw new IOException("Unsaved changes detected.");
        }
        File newPath = state.getPath().resolve(args[0]).toFile();
        if (newPath.exists()) {
            Utils.dumpTable(table);
            if (table != null) {
                table.clear();
            }
            state.setCurrentTable(args[0]);
            Utils.readTable(state.getCurrentTable());
            System.out.println("using " + args[0]);
        } else {
            System.out.println(args[0] + " not exists");
        }
    }
}