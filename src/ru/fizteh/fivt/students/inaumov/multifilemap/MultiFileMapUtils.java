package ru.fizteh.fivt.students.inaumov.multifilemap;

import java.io.File;

public class MultiFileMapUtils {
    public static void deleteFile(File fileToDelete) {
        if (!fileToDelete.exists()) {
            return;
        }
        if (fileToDelete.isDirectory()) {
            for (final File file: fileToDelete.listFiles()) {
                deleteFile(file);
            }
        }
        fileToDelete.delete();
    }
}
