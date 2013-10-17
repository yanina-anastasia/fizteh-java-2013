package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.FileNotFoundException;

public class CommandPut implements Command {
    public String getName() {
        return "put";
    }

    public int getArgCount() {
        return 2;
    }

    public void run(State state, String[] args) throws FileNotFoundException {
        if (state.hasKey(args[0])) {
            System.out.println("overwrite\n" + state.getValue(args[0]));
        } else {
            System.out.println("new");
        }
        state.putValue(args[0], args[1]);
    }
}