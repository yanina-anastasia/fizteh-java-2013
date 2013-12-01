package ru.fizteh.fivt.students.baranov.shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class Remove extends BasicCommand {
    public boolean doCommand(String[] args, ShellState currentPath) {
        if (args.length != 2) {
            System.err.println("rm needs 1 argument");
            return true;
        }

        Path tempPath = Paths.get(args[1]).normalize();
        Path newPath = currentPath.getCurrentPath().resolve(tempPath);
        newPath = newPath.toAbsolutePath();

        if (!Files.exists(newPath)) {
            System.err.println("file doesn't already exist: " + newPath.toString());
            return true;
        }
        if (!Files.isDirectory(newPath)) {
            try {
                Files.delete(newPath);
            } catch (IOException e) {
                System.err.println("can't delete file: " + newPath.toString());
            }
            return true;
        } else {
            FileTreeRemove fileTree = new FileTreeRemove(newPath);
            try {
                Files.walkFileTree(newPath, fileTree);
            } catch (IOException e) {
                System.err.println("error in removing directory" + newPath.toString());
            }

            try {
                Files.delete(newPath);
            } catch (IOException e) {
                System.err.println("can't delete directory: " + newPath.toString());
            }
            return true;
        }
    }
}
