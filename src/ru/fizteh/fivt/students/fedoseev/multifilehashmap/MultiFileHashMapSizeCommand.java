package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class MultiFileHashMapSizeCommand extends AbstractCommand<MultiFileHashMapState> {
    public MultiFileHashMapSizeCommand() {
        super("size", 0);
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) throws IOException {
        MultiFileHashMapTable curTable = state.getCurTable();

        if (curTable == null) {
            throw new IOException("no table");
        } else {
            System.out.println(curTable.size());
        }
    }
}
