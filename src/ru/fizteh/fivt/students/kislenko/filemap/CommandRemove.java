package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class CommandRemove implements Command {
    RandomAccessFile database;

    public String getName() {
        return "remove";
    }

    public int getArgCount() {
        return 1;
    }

    public void run(State state, String[] args) throws FileNotFoundException {
        database = new RandomAccessFile(state.getPath().toFile(), "rw");
        if (state.hasKey(args[0])) {
            state.delValue(args[0]);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}