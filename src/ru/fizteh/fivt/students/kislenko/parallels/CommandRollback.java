package ru.fizteh.fivt.students.kislenko.parallels;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandRollback implements Command<StoreableState> {

    @Override
    public String getName() {
        return "rollback";
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
        System.out.println(state.getCurrentTable().rollback());
    }
}
