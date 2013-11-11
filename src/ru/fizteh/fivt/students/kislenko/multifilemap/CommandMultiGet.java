package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.filemap.CommandGet;
import ru.fizteh.fivt.students.kislenko.filemap.FilemapState;
import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandMultiGet implements Command<MultiFileHashMapState> {
    public String getName() {
        return "get";
    }

    public int getArgCount() {
        return 1;
    }

    public void run(MultiFileHashMapState state, String[] args) throws Exception {
        MyTable table = state.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
            throw new IOException("Database haven't initialized.");
        }
        Command<FilemapState> getter = new CommandGet();
        FilemapState fmState = new FilemapState(null);

        TwoLayeredString key = new TwoLayeredString(args[0]);
        Utils.loadFile(table, key);

        fmState.setMap(table.getMap());
        getter.run(fmState, args);
    }
}