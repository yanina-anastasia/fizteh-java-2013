package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CommandPut implements Command<MultiFileHashMapState> {
    public String getName() {
        return "put";
    }

    public int getArgCount() {
        return 2;
    }

    public void run(MultiFileHashMapState state, String[] args) throws IOException {
        Table table = state.getCurrentTable();
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
            throw new IOException("Database haven't initialized.");
        }
        Utils.connectFile(table, args[0]);
        String oldValue = table.put(args[0], args[1]);
        if (oldValue != null) {
            System.out.println("overwrite\n" + oldValue);
        } else {
            System.out.println("new");
        }
    }
}