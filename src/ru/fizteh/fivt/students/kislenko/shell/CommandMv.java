package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandMv implements Command {
    private static void moveFile(File source, File dest) throws IOException {
        Path target = dest.toPath().resolve(source.getName());
        if (source.isFile()) {
            Files.copy(source.toPath(), target);
        } else {
            File[] sourceEntries = source.listFiles();
            target.toFile().mkdir();
            for (File sourceEntry : sourceEntries) {
                moveFile(sourceEntry, target.toFile());
            }
        }
        source.delete();
    }

    private static Path validatePath(Path path) {
        File f = path.toFile();
        while (!f.isDirectory()) {
            path = path.getParent();
            f = path.toFile();
        }
        return path;
    }

    public void run(String args) throws IOException {
        int endOfFirstArg = args.indexOf(' ');
        String source = args.substring(0, endOfFirstArg);
        String dest = args.substring(endOfFirstArg + 1);
        Path absolutePath = Shell.absolutePath;
        Path sourcePath = absolutePath.resolve(source).normalize();
        Path destPath = absolutePath.resolve(dest).normalize();
        if (destPath.toString().equals(sourcePath)) {
            throw new IOException("mv: Cannot move file on itself.");
        }
        if (sourcePath.toFile().isFile() && !destPath.toFile().isDirectory()) {
            destPath.toFile().delete();
            Files.copy(sourcePath, destPath);
            sourcePath.toFile().delete();
        } else if (sourcePath.toFile().isFile() && destPath.toFile().isDirectory()) {
            moveFile(sourcePath.toFile(), destPath.toFile());
        } else if (sourcePath.toFile().isDirectory() && destPath.toFile().isFile()) {
            throw new IOException("mv: Cannot move directory to file");
        } else if (sourcePath.toFile().isDirectory() && destPath.toFile().isDirectory()) {
            if (destPath.startsWith(sourcePath)) {
                throw new IOException("mv: Cannot move directory on itself.");
            }
            moveFile(sourcePath.toFile(), destPath.toFile());
        } else if (sourcePath.toFile().isDirectory()) {
            File[] sourceEntries = sourcePath.toFile().listFiles();
            destPath.toFile().mkdir();
            for (File sourceEntry : sourceEntries) {
                moveFile(sourceEntry, destPath.toFile());
            }
            sourcePath.toFile().delete();
            Shell.absolutePath = validatePath(absolutePath);
        } else {
            throw new IOException("mv: Incorrect file names.");
        }
    }
}