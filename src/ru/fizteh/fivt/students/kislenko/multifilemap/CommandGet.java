package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CommandGet implements Command<MultiFileHashMapState> {
    public String getName() {
        return "get";
    }

    public int getArgCount() {
        return 1;
    }

    public void run(MultiFileHashMapState state, String[] args) throws IOException {
        Table table = state.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
            throw new IOException("Database haven't initialized.");
        }
        Utils.connectFile(table, args[0]);
        String value = table.get(args[0]);
        if (value != null) {
            System.out.println("found\n" + value);
        } else {
            System.out.println("not found");
        }
    }
}