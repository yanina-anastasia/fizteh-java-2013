package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.IOException;
import java.text.ParseException;

public class MultiFileHashMapPutCommand extends AbstractCommand<State> {
    public MultiFileHashMapPutCommand() {
        super("put", 2);
    }

    @Override
    public void execute(String[] input, State state) throws IOException, ParseException {
        if (state.getCurTable() == null) {
            throw new IOException("no table");
        } else {
            String putEntry = state.put(input[0], input[1]);

            if (putEntry == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite\n" + putEntry);
            }
        }
    }
}
