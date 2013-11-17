package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class MultiFileHashMapSizeCommand extends AbstractCommand<MultiFileHashMapState> {
    public MultiFileHashMapSizeCommand() {
        super("size", 0);
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) throws IOException {
        MultiFileHashMapTable table = state.getCurTable();

        if (table == null) {
            System.out.println("no table");
            throw new IOException("ERROR: not existing table");
        } else {
            System.out.println(table.size());
        }
    }
}
