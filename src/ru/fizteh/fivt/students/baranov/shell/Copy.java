package ru.fizteh.fivt.students.baranov.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Copy extends BasicCommand {
    public int doCommand(String[] args, ShellState currentPath) {
        if (args.length != 3) {
            System.err.println("copy needs 2 arguments");
            return 1;
        }

        Path tempPath = Paths.get(args[1]).normalize();
        Path sourcePath = currentPath.getCurrentPath().resolve(tempPath);
        sourcePath = sourcePath.toAbsolutePath();

        tempPath = Paths.get(args[2]).normalize();
        Path targetPath = currentPath.getCurrentPath().resolve(tempPath);
        targetPath = targetPath.toAbsolutePath();

        if (!Files.exists(sourcePath)) {
            System.err.println("source doesn't exist: " + sourcePath.toString());
            return 1;
        }

        if (!Files.exists(targetPath)) {
            System.err.println("target doesn't exist: " + targetPath.toString());
            return 1;
        }

        if ((!Files.isDirectory(sourcePath) && !Files.isDirectory(targetPath))
                || (!Files.isDirectory(sourcePath) && Files.isDirectory(targetPath))) {
            try {
                Files.copy(sourcePath, targetPath.resolve(sourcePath.getFileName()));
            } catch (IOException e) {
                System.err.println("error in copying files");
                return 1;
            }
            currentPath.copyMade = 1;
            return 2;
        } else if (Files.isDirectory(sourcePath) && Files.isDirectory(targetPath)) {
            FileTreeCopy fileTree = new FileTreeCopy(sourcePath, targetPath);
            try {
                Files.walkFileTree(sourcePath, fileTree);
            } catch (IOException e) {
                System.err.println("error in copying directories");
                return 1;
            }
            currentPath.copyMade = 1;
            return 2;
        } else if (Files.isDirectory(sourcePath) && !Files.isDirectory(targetPath)) {
            System.err.println("copying directory to file");
            return 1;
        }
        currentPath.copyMade = 1;
        return 2;
    }
}
