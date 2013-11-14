package ru.fizteh.fivt.students.kislenko.parallels;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandStorableGet implements Command<StoreableState> {
    public String getName() {
        return "get";
    }

    public int getArgCount() {
        return 1;
    }

    public void run(StoreableState state, String[] args) throws Exception {
        MyTable table = state.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
            throw new IOException("Database haven't initialized.");
        }

        if (state.getCurrentTable().get(args[0]) != null) {
            String result = table.getProvider().serialize(table, table.get(args[0]));
            System.out.println("found\n" + result);
        } else {
            System.out.println("not found");
        }
    }
}
