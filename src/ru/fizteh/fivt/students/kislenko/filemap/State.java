package ru.fizteh.fivt.students.kislenko.filemap;

import java.nio.file.Path;

public class State {
    private Path absolutePath;

    public Path getState() {
        return absolutePath;
    }

    public void setState(Path path) {
        absolutePath = path;
    }
}