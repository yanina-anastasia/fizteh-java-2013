package ru.fizteh.fivt.students.kislenko.junit;

import java.io.File;
import java.io.IOException;

public class MultiFilemapBuilder {
    public void build(MultiFileHashMapState state) {
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

    public void finish(MultiFileHashMapState state) throws IOException {
        if (state.getCurrentTable() != null) {
            Utils.dumpTable(state.getCurrentTable());
        }
    }
}