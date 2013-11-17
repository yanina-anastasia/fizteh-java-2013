package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class MultiFileHashMapGetCommand extends AbstractCommand<MultiFileHashMapState> {
    public MultiFileHashMapGetCommand() {
        super("get", 1);
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) throws IOException {
        MultiFileHashMapTable curTable = state.getCurTable();

        if (curTable == null) {
            System.out.println("no table");
            throw new IOException("ERROR: not existing table");
        } else {
            String gotValue = curTable.get(input[0]);

            if (gotValue == null) {
                System.out.println("not found");
            } else {
                System.out.println("found\n" + gotValue);
            }
        }
    }
}
