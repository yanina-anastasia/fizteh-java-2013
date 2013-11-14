package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class StoreableExitCommand extends AbstractCommand<StoreableState> {
    public StoreableExitCommand() {
        super("exit", 0);
    }

    @Override
    public void execute(String[] input, StoreableState state) throws IOException {
        AbstractStoreable.saveTable(state.getCurTable());

        Thread.currentThread().interrupt();
    }
}
