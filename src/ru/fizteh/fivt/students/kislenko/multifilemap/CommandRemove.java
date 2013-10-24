package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandRemove implements Command<MultiFileHashMapState> {
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
        TwoLayeredString key = new TwoLayeredString(args[0]);
        Utils.loadFile(table, key);
        String value = table.get(args[0]);
        if (value != null) {
            state.getCurrentTable().remove(args[0]);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}