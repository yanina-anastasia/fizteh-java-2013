package ru.fizteh.fivt.students.kislenko.parallels;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;
import java.text.ParseException;

public class CommandStoreablePut implements Command<StoreableState> {
    public String getName() {
        return "put";
    }

    public int getArgCount() {
        return 2;
    }

    public void run(StoreableState state, String[] args) throws Exception {
        MyTable table = state.getCurrentTable();
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
            throw new IOException("Database haven't initialized.");
        }

        try {
            Storeable oldValue = table.get(args[0]);
            if (oldValue != null) {
                Storeable value = table.getProvider().deserialize(table, args[1]);
                String result = table.getProvider().serialize(table, table.put(args[0], value));
                System.out.println("overwrite\n" + result);
            } else {
                Storeable value = table.getProvider().deserialize(table, args[1]);
                table.put(args[0], value);
                System.out.println("new");
            }
        } catch (ParseException e) {
            System.out.println("wrong type " + e.getMessage());
            throw e;
        }
    }
}
