package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;

public class MultiFileHashMapCreateCommand extends AbstractCommand<MultiFileHashMapState> {
    public MultiFileHashMapCreateCommand() {
        super("create", 1);
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) {
        File tableDir = state.getCurDir().toPath().resolve(input[0]).toFile();

        if (!tableDir.exists()) {
            state.createTable(input[0]);
            tableDir.mkdirs();

            System.out.println("created");
        } else {
            System.out.println(input[0] + " exists");
        }
    }
}
