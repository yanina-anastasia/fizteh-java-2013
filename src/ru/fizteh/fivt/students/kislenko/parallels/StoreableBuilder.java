package ru.fizteh.fivt.students.kislenko.parallels;

import java.io.File;
import java.io.IOException;

public class StoreableBuilder {
    public void build(StoreableState state) throws IOException, ClassNotFoundException {
        File dirCreator = state.getWorkingPath().toFile();
        if (!dirCreator.exists()) {
            dirCreator.mkdir();
        }
        if (dirCreator.listFiles() != null) {
            for (File file : dirCreator.listFiles()) {
                state.createTable(file.getName());
            }
        }
    }

    public void finish(StoreableState state) throws IOException {
        if (state.getCurrentTable() != null) {
            Utils.dumpTable(state.getCurrentTable());
        }
    }
}
