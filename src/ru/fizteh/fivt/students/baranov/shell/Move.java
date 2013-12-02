package ru.fizteh.fivt.students.baranov.shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Move extends BasicCommand {
    public boolean doCommand(String[] args, ShellState currentPath) {
        if (args.length != 3) {
            System.err.println("mv needs 2 arguments");
            return true;
        }

        Path tempPath = Paths.get(args[1]).normalize();
        Path sourcePath = currentPath.getCurrentPath().resolve(tempPath);
        sourcePath = sourcePath.toAbsolutePath();

        tempPath = Paths.get(args[2]).normalize();
        Path targetPath = currentPath.getCurrentPath().resolve(tempPath);
        targetPath = targetPath.toAbsolutePath();

        if (!Files.exists(sourcePath)) {
            System.err.println("source doesn't exist: " + sourcePath.toString());
            return true;
        }

        if (Files.isDirectory(sourcePath) || Files.isDirectory(targetPath)) {
            Copy cp = new Copy();
            Remove rm = new Remove();
            String[] newArgs = {args[0], args[1]};
            cp.doCommand(args, currentPath);
            if (currentPath.copyMade == 1) {
                rm.doCommand(newArgs, currentPath);
            }
        } else {
            Path sourceParent = sourcePath.getParent();
            Path targetParent = targetPath.getParent();
            if (sourceParent.equals(targetParent)) {
                if (!targetPath.toFile().renameTo(sourcePath.toFile())) {
                    System.err.println("can't rename " + targetPath.toString());
                    System.err.println("to " + sourcePath.toString());
                    return true;
                }
            }
        }

        return true;
    }
}
