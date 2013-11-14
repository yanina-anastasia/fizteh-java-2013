package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;
import java.text.ParseException;

public class StoreableUseCommand extends AbstractCommand<StoreableState> {
    public StoreableUseCommand() {
        super("use", 1);
    }

    @Override
    public void execute(String[] input, StoreableState state) throws IOException, ParseException {
        String tableName = input[0];

        if (state.getCurDir().getName().equals(tableName)) {
            System.out.println("using " + tableName);
            return;
        }

        StoreableTable curTable = state.getCurTable();
        int changesNumber = 0;

        if (curTable != null) {
            changesNumber = curTable.getDiffSize();
        }

        if (changesNumber != 0) {
            throw new IOException(changesNumber + " unsaved changes");
        }

        if (state.getCurDir().toPath().resolve(tableName).toFile().exists()) {
            AbstractStoreable.saveTable(curTable);

            if (curTable != null) {
                curTable.clearContentAndDiff();
            }

            state.setCurTable(tableName);
            curTable = state.getCurTable();

            AbstractStoreable.readTableOff(curTable);

            System.out.println("using " + tableName);
        } else {
            System.out.println(tableName + " not exists");
        }
    }
}
