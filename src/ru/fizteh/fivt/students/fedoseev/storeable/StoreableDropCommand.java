package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.IOException;

public class StoreableDropCommand extends AbstractCommand<StoreableState> {
    public StoreableDropCommand() {
        super("drop", 1);
    }

    @Override
    public void execute(String[] input, StoreableState state) throws IOException {
        String tableName = input[0];
        File curTableDir = new File(state.getCurDir(), tableName);
        StoreableTable curTable = state.getCurTable();

        if (curTableDir.exists()) {
            if (curTableDir.listFiles() != null) {
                for (File dir : curTableDir.listFiles()) {
                    if (dir.listFiles() != null) {
                        for (File file : dir.listFiles()) {
                            file.delete();
                        }
                    }

                    dir.delete();
                }
            }

            if (curTable != null && tableName.equals(curTable.getCurTableDir().getName())) {
                curTable.clearContentAndDiff();
                state.setCurTable(null);
            }

            curTableDir.delete();
            state.removeTable(curTableDir.toString());

            System.out.println("dropped");
        } else {
            System.out.println(tableName + " not exists");
        }
    }
}
