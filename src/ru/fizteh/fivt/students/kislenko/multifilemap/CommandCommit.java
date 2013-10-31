package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandCommit implements Command<MultiFileHashMapState> {

    @Override
    public String getName() {
        return "commit";
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @Override
    public void run(MultiFileHashMapState state, String[] args) throws IOException {
        state.getCurrentTable().updateSize();

    }
}
