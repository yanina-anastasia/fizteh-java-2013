package ru.fizteh.fivt.students.kislenko.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandStorableRemove implements Command<StoreableState> {
    public String getName() {
        return "remove";
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
        Storeable oldValue = table.remove(args[0]);
        if (oldValue != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
