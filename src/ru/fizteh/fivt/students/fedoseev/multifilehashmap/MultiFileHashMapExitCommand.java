package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.IOException;

public class MultiFileHashMapExitCommand extends AbstractCommand<State> {
    public MultiFileHashMapExitCommand() {
        super("exit", 0);
    }

    @Override
    public void execute(String[] input, State state) throws IOException {
        state.saveTable(state.getCurTable());

        Thread.currentThread().interrupt();
    }
}
