package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapDropCommand extends AbstractCommand<MultiFileHashMapState> {
    public MultiFileHashMapDropCommand() {
        super("drop", 1);
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) throws IOException {
        String tableName = input[0];
        File curTableDir = state.getCurDir().toPath().resolve(tableName).toFile();

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

            if (state.getCurTable() != null &&
                    tableName.equals(state.getCurTable().getCurTableDir().getName().toString())) {
                state.getCurTable().clearContentAndDiff();
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
