package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileMapState {
    private List<Table> tables;

    public FileMapState(File directory) throws IOException {
        if (!directory.exists()) {
            throw new IOException("file doesn't exist");
        }
        if (directory.isDirectory()) {
            File[] tableFolders = directory.listFiles();
            for (File tableFolder : tableFolders) {

            }
        }
    }
}
