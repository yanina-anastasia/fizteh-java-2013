package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class StoreableRemoveCommand extends AbstractCommand<StoreableState> {
    public StoreableRemoveCommand() {
        super("remove", 1);
    }

    @Override
    public void execute(String[] input, StoreableState state) throws IOException {
        StoreableTable curTable = state.getCurTable();

        if (curTable == null) {
            throw new IOException("no table");
        } else {
            Storeable removedValue = curTable.remove(input[0]);

            if (removedValue == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        }
    }
}
