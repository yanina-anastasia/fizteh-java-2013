package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandSize implements Command<MultiFileHashMapState> {

    @Override
    public String getName() {
        return "size";
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @Override
    public void run(MultiFileHashMapState state, String[] args) throws IOException {
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
            throw new IOException("Database haven't initialized.");
        }
        System.out.println(state.getCurrentTable().size());
    }
}
