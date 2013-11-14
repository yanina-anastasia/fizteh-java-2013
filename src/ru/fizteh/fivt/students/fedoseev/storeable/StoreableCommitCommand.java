package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class StoreableCommitCommand extends AbstractCommand<StoreableState> {
    public StoreableCommitCommand() {
        super("commit", 0);
    }

    @Override
    public void execute(String[] input, StoreableState state) throws IOException {
        StoreableTable curTable = state.getCurTable();

        if (curTable == null) {
            throw new IOException("no table");
        } else {
            AbstractStoreable.saveTable(curTable);

            System.out.println(curTable.commit());
        }
    }
}
