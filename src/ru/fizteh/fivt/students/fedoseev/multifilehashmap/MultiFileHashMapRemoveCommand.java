package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class MultiFileHashMapRemoveCommand extends AbstractCommand<MultiFileHashMapState> {
    public MultiFileHashMapRemoveCommand() {
        super("remove", 1);
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) throws IOException {
        MultiFileHashMapTable curTable = state.getCurTable();

        if (curTable == null) {
            System.out.println("no table");
            throw new IOException("ERROR: not existing table");
        } else {
            if (curTable.get(input[0]) == null) {
                System.out.println("not found");
            } else {
                curTable.remove(input[0]);

                System.out.println("removed");
            }
        }
    }
}
