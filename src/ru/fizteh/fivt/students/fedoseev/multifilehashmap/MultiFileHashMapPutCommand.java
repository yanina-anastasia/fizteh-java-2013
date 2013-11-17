package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class MultiFileHashMapPutCommand extends AbstractCommand<MultiFileHashMapState> {
    public MultiFileHashMapPutCommand() {
        super("put", 2);
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) throws IOException {
        MultiFileHashMapTable curTable = state.getCurTable();

        if (curTable == null) {
            System.out.println("no table");
            throw new IOException("ERROR: not existing table");
        } else {
            String putEntry = curTable.put(input[0], input[1]);

            if (putEntry == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite\n" + putEntry);
            }
        }
    }
}
