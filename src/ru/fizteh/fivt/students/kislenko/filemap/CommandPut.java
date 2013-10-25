package ru.fizteh.fivt.students.kislenko.filemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.FileNotFoundException;

public class CommandPut implements Command<FilemapState> {
    public String getName() {
        return "put";
    }

    public int getArgCount() {
        return 2;
    }

    public void run(FilemapState state, String[] args) throws FileNotFoundException {
        if (state.hasKey(args[0])) {
            System.out.println("overwrite\n" + state.getValue(args[0]));
        } else {
            System.out.println("new");
        }
        state.putValue(args[0], args[1]);
    }
}