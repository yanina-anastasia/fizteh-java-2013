package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class StoreableGetCommand extends AbstractCommand<StoreableState> {
    public StoreableGetCommand() {
        super("get", 1);
    }

    @Override
    public void execute(String[] input, StoreableState state) throws IOException {
        StoreableTable curTable = state.getCurTable();

        if (curTable == null) {
            throw new IOException("no table");
        } else {
            Storeable gotValue = curTable.get(input[0]);

            if (gotValue == null) {
                System.out.println("not found");
            } else {
                System.out.println("found\n" + curTable.getTb().serialize(curTable, gotValue));
            }
        }
    }
}
