package ru.fizteh.fivt.students.kislenko.filemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;
import ru.fizteh.fivt.students.kislenko.shell.State;

import java.io.FileNotFoundException;

public class CommandGet implements Command {
    public String getName() {
        return "get";
    }

    public int getArgCount() {
        return 1;
    }

    public void run(State state, String[] args) throws FileNotFoundException {
        if (((FilemapState) state).hasKey(args[0])) {
            System.out.println("found\n" + ((FilemapState) state).getValue(args[0]));
        } else {
            System.out.println("not found");
        }
    }
}