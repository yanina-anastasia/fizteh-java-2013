package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandMultiPut implements Command<MultiFileHashMapState> {
    public String getName() {
        return "put";
    }

    public int getArgCount() {
        return 2;
    }

    public void run(MultiFileHashMapState state, String[] args) throws IOException {
        MyTable table = state.getCurrentTable();
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
            throw new IOException("Database haven't initialized.");
        }
        String result = table.put(args[0], args[1]);

        if (result != null) {
            System.out.println("overwrite\n" + result);
        } else {
            System.out.println("new");
        }
    }
}