package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.filemap.CommandPut;
import ru.fizteh.fivt.students.kislenko.filemap.FilemapState;
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
        Command<FilemapState> putter = new CommandPut();
        FilemapState fmState = new FilemapState(null);

        TwoLayeredString key = new TwoLayeredString(args[0]);
        Utils.loadFile(table, key);
        fmState.setMap(table.getMap());

        putter.run(fmState, args);

        table.setMap(fmState.getMap());
    }
}