package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.filemap.CommandRemove;
import ru.fizteh.fivt.students.kislenko.filemap.FilemapState;
import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandMultiRemove implements Command<MultiFileHashMapState> {
    public String getName() {
        return "remove";
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
        Command<FilemapState> remover = new CommandRemove();
        FilemapState fmState = new FilemapState(null);

        TwoLayeredString key = new TwoLayeredString(args[0]);
        Utils.loadFile(table, key);
        fmState.setMap(table.getMap());

        remover.run(fmState, args);

        table.setMap(fmState.getMap());
    }
}