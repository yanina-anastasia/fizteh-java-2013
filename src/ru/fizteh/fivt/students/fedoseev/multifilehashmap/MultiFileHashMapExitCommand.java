package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class MultiFileHashMapExitCommand extends AbstractCommand<MultiFileHashMapState> {
    public MultiFileHashMapExitCommand() {
        super("exit", 0);
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) throws IOException {
        AbstractMultiFileHashMap.saveTable(state.getCurTable());

        Thread.currentThread().interrupt();
    }
}
