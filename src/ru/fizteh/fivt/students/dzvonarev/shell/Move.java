package ru.fizteh.fivt.students.dzvonarev.shell;

import java.io.File;
import java.io.IOException;

public class Move {

    private static boolean isMoved(String source, String destination) {
        File sourceFile = new File(source);
        if (!sourceFile.isFile() && !sourceFile.isDirectory()) {
            return false;
        }
        if (sourceFile.isFile() || sourceFile.isDirectory()) {
            File destFile = new File(destination);
            if (destFile.isDirectory()) {
                return sourceFile.renameTo(new File(destination + File.separator + sourceFile.getName()));
            } else {
                return !destFile.isFile() && sourceFile.renameTo(new File(destination));
            }
        }
        return true;
    }

    public static void moveObject(String expr, int spaceIndex) throws IOException {
        int newSpaceIndex = expr.indexOf(' ', spaceIndex + 1);
        if (newSpaceIndex == -1) {
            throw new IOException("mv: wrong parameters");
        }
        int index = newSpaceIndex;
        while (expr.indexOf(' ', newSpaceIndex + 1) == newSpaceIndex + 1) {
            ++newSpaceIndex;
        }
        if (expr.indexOf(' ', newSpaceIndex + 1) != -1) {
            throw new IOException("mv: wrong parameters");
        }
        String source = DoCommand.getAbsPath(expr.substring(spaceIndex + 1, index));
        String destination = DoCommand.getAbsPath(expr.substring(newSpaceIndex + 1, expr.length()));
        if (destination.contains(source)) {    // if parent into child
            throw new IOException("mv: can't move " + source);
        }
        if (!isMoved(source, destination)) {
            throw new IOException("mv: can't move " + source);
        }
    }

}
