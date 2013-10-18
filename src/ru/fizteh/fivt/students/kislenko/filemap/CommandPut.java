package ru.fizteh.fivt.students.kislenko.filemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;
import ru.fizteh.fivt.students.kislenko.shell.State;

import java.io.FileNotFoundException;

public class CommandPut implements Command {
    public String getName() {
        return "put";
    }

    public int getArgCount() {
        return 2;
    }

    public void run(State state, String[] args) throws FileNotFoundException {
        if (((FilemapState) state).hasKey(args[0])) {
            System.out.println("overwrite\n" + ((FilemapState) state).getValue(args[0]));
        } else {
            System.out.println("new");
        }
        ((FilemapState) state).putValue(args[0], args[1]);
    }
}