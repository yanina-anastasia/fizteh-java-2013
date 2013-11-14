package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;
import java.text.ParseException;

public class StoreablePutCommand extends AbstractCommand<StoreableState> {
    public StoreablePutCommand() {
        super("put", 2);
    }

    @Override
    public void execute(String[] input, StoreableState state) throws IOException, ParseException {
        StoreableTable curTable = state.getCurTable();

        if (curTable == null) {
            throw new IOException("no table");
        } else {
            try {
                Storeable curValue = curTable.get(input[0]);
                Storeable newValue = curTable.getTb().deserialize(curTable, input[1]);
                Storeable putEntry = curTable.put(input[0], newValue);

                if (curValue == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite\n" + curTable.getTb().serialize(curTable, putEntry));
                }
            } catch (ParseException e) {
                System.out.println("wrong type (" + e.getMessage() + ")");

                throw e;
            }
        }
    }
}
