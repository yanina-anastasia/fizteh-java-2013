package ru.fizteh.fivt.students.kislenko.multifilemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Set;

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
            Utils.fillTable(state.getCurrentTable());
        }
    }
}