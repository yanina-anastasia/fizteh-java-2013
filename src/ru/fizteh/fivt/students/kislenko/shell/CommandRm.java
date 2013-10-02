package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class CommandRm implements Command {
    private static void removing(Path target) throws IOException {
        File delFile = target.toFile();
        if (delFile.isFile()) {
            if (!delFile.delete()) {
                throw new IOException("rm: Cannot remove " + delFile.getName() + ".");
            }
        } else if (delFile.isDirectory()) {
            File[] filesInDir = delFile.listFiles();
            for (File fileInDir : filesInDir != null ? filesInDir : new File[0]) {
                removing(target.resolve(fileInDir.getName()));
            }
            if (!delFile.delete()) {
                throw new IOException("rm: Cannot remove " + delFile.getName() + ".");
            }
        }
    }

    private static Path validatePath(Path path) {
        File f = path.toFile();
        while (!f.isDirectory()) {
            path = path.getParent();
            f = path.toFile();
        }
        return path;
    }

    public void run(String s) throws IOException {
        String[] args = s.split("  *");
        if (args.length > 2) {
            throw new IOException("rm: Too many arguments.");
        } else if (args.length < 2) {
            throw new IOException("rm: Too few arguments.");
        }
        String fileName = args[1];
        Path absolutePath = Location.getPath();
        Path target = absolutePath.resolve(fileName);
        removing(target);
        Location.changePath(validatePath(absolutePath));
    }
}