package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import java.io.*;
import java.lang.System;
import java.nio.file.*;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class CopyCommand extends Command {
    public Path getPath(String input, ShellState curState) {
        Path curPath = Paths.get(input);
        if (curState.workingDirectory != null && !curPath.isAbsolute()) {
            curPath = Paths.get(curState.workingDirectory).resolve(curPath);
        }
        curPath = curPath.normalize();
        return curPath;
    }

    public boolean exec(String[] args, ShellState curState) throws IOException {
        if (args.length != 2) {
            System.err.println("Invalid arguments");
            return false;
        }

        final Path sourcePath = getPath(args[0], curState);
        final Path destinationPath = getPath(args[1], curState);
        final Path sourceParentPath = sourcePath.getParent();
        Files.walkFileTree(sourcePath, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relative = sourceParentPath.relativize(dir);
                Path destinationDir = destinationPath.resolve(relative);
                if (!destinationDir.equals(destinationPath)) {
                    Files.createDirectory(destinationDir);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = sourceParentPath.relativize(file);
                Path destinationFile = destinationPath.resolve(relative);
                if (Files.exists(destinationFile)) {
                    throw new IOException("File already exists");
                }
                Files.copy(file, destinationFile);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
        return true;
    }

    public String getCmd() {
        return "cp";
    }
}