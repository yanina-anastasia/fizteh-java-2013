package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapUseCommand extends AbstractCommand<MultiFileHashMapState> {
    public MultiFileHashMapUseCommand() {
        super("use", 1);
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) throws IOException {
        String tableName = input[0];

        if (state.getCurDir().getName().equals(tableName)) {
            System.out.println("using " + tableName);
            return;
        }

        MultiFileHashMapTable curTable = state.getCurTable();

        File newDir = state.getCurDir().toPath().resolve(tableName).toFile();

        if (newDir.exists()) {
            AbstractMultiFileHashMap.saveTable(curTable);

            if (curTable != null) {
                curTable.clearContentAndDiff();
            }

            state.setCurTable(input[0]);
            AbstractMultiFileHashMap.readTableOff(state.getCurTable());

            System.out.println("using " + tableName);
        } else {
            System.out.println(tableName + " not exists");
        }
    }
}
