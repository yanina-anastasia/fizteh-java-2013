package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.filemap.CommandGet;
import ru.fizteh.fivt.students.kislenko.filemap.FilemapState;
import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandMultiGet implements Command<MultiFileHashMapState> {
    public String getName() {
        return "get";
    }

    public int getArgCount() {
        return 1;
    }

    public void run(MultiFileHashMapState state, String[] args) throws IOException {
        MyTable table = state.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
            throw new IOException("Database haven't initialized.");
        }
        String result = state.getCurrentTable().get(args[0]);

        if (state.getCurrentTable().get(args[0]) != null) {
            System.out.println("found\n" + result);
        } else {
            System.out.println("not found");
        }
    }
}