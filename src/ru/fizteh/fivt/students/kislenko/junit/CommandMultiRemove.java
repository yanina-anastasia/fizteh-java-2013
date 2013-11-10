package ru.fizteh.fivt.students.kislenko.junit;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandMultiRemove implements Command<MultiFileHashMapState> {
    public String getName() {
        return "remove";
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
        String result = table.remove(args[0]);

        if (result != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}