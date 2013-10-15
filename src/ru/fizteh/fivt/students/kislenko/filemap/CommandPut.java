package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class CommandPut implements Command {
    RandomAccessFile database;

    public String getName() {
        return "put";
    }

    public int getArgCount() {
        return 2;
    }

    public void run(State state, String[] args) throws FileNotFoundException {
        database = new RandomAccessFile(state.getPath().toFile(), "rw");
        if (state.hasKey(args[0])) {
            System.out.println("overwrite\n" + state.getValue(args[0]));
        } else {
            System.out.println("new");
        }
        state.putValue(args[0], args[1]);
    }
}