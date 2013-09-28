package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;

public class DeleteFunctions {

    public static void deleteDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                deleteDirectory(f);
            }
        }
        boolean success = directory.delete();
        if (!success) {
            throw new IOException("cannot remove " + directory.getName() + ": unknown error");
        }
    }
}
