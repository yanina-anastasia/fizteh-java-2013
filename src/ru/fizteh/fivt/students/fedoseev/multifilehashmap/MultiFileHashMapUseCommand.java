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
        if (state.getCurDir().getName().toString().equals(input[0])) {
            return;
        }

        MultiFileHashMapTable table = state.getCurTable();
        File newDir = state.getCurDir().toPath().resolve(input[0]).toFile();

        if (newDir.exists()) {
            AbstractMultiFileHashMap.commitTable(table);

            if (!state.getCurTableName().equals("")) {
                if (table != null) {
                    table.clearContent();
                }
            }

            state.setDbDir(input[0]);
            state.setCurTable(input[0]);

            System.out.println("using " + input[0]);
        } else {
            System.out.println(input[0] + " not exists");
        }
    }
}
