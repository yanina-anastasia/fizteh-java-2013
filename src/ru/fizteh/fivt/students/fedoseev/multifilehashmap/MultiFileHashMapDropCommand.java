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
        File tableDir = state.getCurDir().toPath().resolve(input[0]).toFile();

        if (tableDir.exists()) {
            if (tableDir.listFiles() != null) {
                for (File dir : tableDir.listFiles()) {
                    if (dir.listFiles() != null) {
                        for (File file : dir.listFiles()) {
                            file.delete();
                        }
                    }

                    dir.delete();
                }
            }

            if (input[0].equals(state.getCurTableName())) {
                state.getCurTable().clearContent();
                state.setCurTable("");
                state.setDbDir("");
            }

            tableDir.delete();
            state.removeTable(input[0]);

            System.out.println("dropped");
        } else {
            System.out.println(input[0] + " not exists");
        }
    }
}
