package ru.fizteh.fivt.students.adanilyak.tools;

import java.io.File;
/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 15:45
 */
public class DeleteDirectory {
    private static void recursiveDeletePart(File startPoint) throws Exception {
        File[] listOfFiles = startPoint.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                recursiveDeletePart(file);
            }
        }

        if (!startPoint.delete()) {
            throw new Exception("Can not delete directory, unknown error");
        }
    }

    public static void rm(File directory) throws Exception {
        if (!directory.exists()) {
            throw new Exception("File or directory do not exist");
        }
        recursiveDeletePart(directory);
    }
}
