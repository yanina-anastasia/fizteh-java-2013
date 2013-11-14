package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class StoreableSizeCommand extends AbstractCommand<StoreableState> {
    public StoreableSizeCommand() {
        super("size", 0);
    }

    @Override
    public void execute(String[] input, StoreableState state) throws IOException {
        StoreableTable curTable = state.getCurTable();

        if (curTable == null) {
            throw new IOException("no table");
        } else {
            System.out.println(curTable.size());
        }
    }
}
