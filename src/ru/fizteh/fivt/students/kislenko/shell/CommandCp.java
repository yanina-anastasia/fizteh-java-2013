package ru.fizteh.fivt.students.kislenko.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandCp implements Command {
    private static void copyFile(File source, File dest) throws IOException {
        Path target = dest.toPath().resolve(source.getName());
        if (source.isFile()) {
            Files.copy(source.toPath(), target);
        } else {
            File[] sourceEntries = source.listFiles();
            target.toFile().mkdir();
            for (File sourceEntry : sourceEntries) {
                copyFile(sourceEntry, target.toFile());
            }
        }
    }

    public void run(String s) throws IOException {
        String[] args = s.split("  *");
        if (args.length > 3) {
            throw new IOException("cp: Too many arguments.");
        } else if (args.length < 3) {
            throw new IOException("cp: Too few arguments.");
        }
        String source = args[1];
        String dest = args[2];
        Path absolutePath = Location.getPath();
        Path sourcePath = absolutePath.resolve(source).normalize();
        Path destPath = absolutePath.resolve(dest).normalize();
        if (destPath.toString().equals(sourcePath.toString())) {
            throw new IOException("cp: Cannot copy file on itself.");
        }
        if (sourcePath.toFile().isFile() && !destPath.toFile().isDirectory()) {
            destPath.toFile().delete();
            Files.copy(sourcePath, destPath);
            sourcePath.toFile().delete();
        } else if (sourcePath.toFile().isFile() && destPath.toFile().isDirectory()) {
            copyFile(sourcePath.toFile(), destPath.toFile());
        } else if (sourcePath.toFile().isDirectory() && destPath.toFile().isFile()) {
            throw new IOException("cp: Cannot copy directory to file");
        } else if (sourcePath.toFile().isDirectory() && destPath.toFile().isDirectory()) {
            if (destPath.startsWith(sourcePath)) {
                throw new IOException("cp: Cannot copy directory on itself.");
            }
            copyFile(sourcePath.toFile(), destPath.toFile());
        } else if (sourcePath.toFile().isDirectory()) {
            File[] sourceEntries = sourcePath.toFile().listFiles();
            destPath.toFile().mkdir();
            for (File sourceEntry : sourceEntries != null ? sourceEntries : new File[0]) {
                copyFile(sourceEntry, destPath.toFile());
            }
        } else {
            throw new IOException("cp: Incorrect file names.");
        }
    }
}