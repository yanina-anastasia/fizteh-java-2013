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

    public void run(String fileName) throws IOException {
        Path absolutePath = Shell.absolutePath;
        Path target = absolutePath.resolve(fileName);
        try {
            removing(target);
        } catch (IOException e) {
            throw e;
        }
        Shell.absolutePath = validatePath(absolutePath);
    }
}