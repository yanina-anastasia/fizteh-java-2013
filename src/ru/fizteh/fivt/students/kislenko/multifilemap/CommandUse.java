package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class CommandUse implements Command<MultiFileHashMapState> {
    public String getName() {
        return "use";
    }

    public int getArgCount() {
        return 1;
    }

    public void run(MultiFileHashMapState state, String[] args) throws IOException {
        if (state.getWorkingPath().getFileName().toString().equals(args[0])) {
            return;
        }
        if (!args[0].matches("([0-9]|1[0-5]).dir")) {
            throw new IOException("Incorrect table name");
        }
        File newPath = state.getPath().resolve(args[0]).toFile();
        if (newPath.exists()) {
            if (state.getCurrentTableController() != null) {
                state.getCurrentTableController().fillTable(state);
            }
            state.getMap().clear();
            state.setWorkingPath(args[0]);
            state.getCurrentTableController().readTable(state);
            state.setWorkingPath(args[0]);
            System.out.println("using " + args[0]);
        } else {
            System.out.println(args[0] + " not exists");
        }
    }
}