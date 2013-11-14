package ru.fizteh.fivt.students.kislenko.parallels;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandCommit implements Command<StoreableState> {

    @Override
    public String getName() {
        return "commit";
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @Override
    public void run(StoreableState state, String[] args) throws Exception {
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
            throw new IOException("Database haven't initialized.");
        }
        Utils.dumpTable(state.getCurrentTable());
        System.out.println(state.getCurrentTable().commit());
    }
}
