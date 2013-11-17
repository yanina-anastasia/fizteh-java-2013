package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.IOException;

public class MultiFileHashMapGetCommand extends AbstractCommand<State> {
    public MultiFileHashMapGetCommand() {
        super("get", 1);
    }

    @Override
    public void execute(String[] input, State state) throws IOException {
        if (state.getCurTable() == null) {
            throw new IOException("no table");
        } else {
            String gotValue = state.get(input[0]);

            if (gotValue == null) {
                System.out.println("not found");
            } else {
                System.out.println("found\n" + gotValue);
            }
        }
    }
}
