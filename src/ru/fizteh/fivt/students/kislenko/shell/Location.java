package ru.fizteh.fivt.students.kislenko.shell;

import java.nio.file.Path;

public class Location {
    private static Path absolutePath;

    public static Path getPath() {
        return absolutePath;
    }

    public static void changePath(Path path) {
        absolutePath = path;
    }
}