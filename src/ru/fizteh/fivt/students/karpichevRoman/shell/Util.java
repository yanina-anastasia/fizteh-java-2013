package ru.fizteh.fivt.students.karpichevRoman.shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.File;

class Util {
    private static class DeleteVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)     
            throws IOException {

                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exception) 
            throws IOException {
                if (exception == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw exception;
                }
        }
    }

    private static class CopyVisitor extends SimpleFileVisitor<Path> {
        private Path destination;
        private Path start;
        
        public CopyVisitor(Path start, Path destination) {
            this.start = start;
            this.destination = destination;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) 
            throws IOException {
                Path newPath = destination.resolve(start.relativize(file));
                System.out.println("copy file " + file);
                System.out.println("copy to " + newPath);
                Files.copy(file, newPath);
                return FileVisitResult.CONTINUE;
            }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) 
            throws IOException {
                System.out.println("in preVisitDirectory");
                Path newPath = destination.resolve(start.relativize(dir));
                Files.copy(dir, newPath);
                return FileVisitResult.CONTINUE;
            }
    }

    private static Path getTargetPath(Path from, Path to) throws IllegalArgumentException {
        try {
            if (new File(to.toAbsolutePath().toString()).exists() && Files.isSameFile(from, to)) {
                throw new IllegalArgumentException("trying to copy to itself");
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("bad path to analyze");
        }

        try {
            if (Files.isDirectory(to)) {
                return to.resolve(from.getFileName());
            } else {
                return to;
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException("bad path to analyze");
        }
    }

    public static void recursiveDelete(Path path) throws IllegalArgumentException {
        try {
            Files.walkFileTree(path, new DeleteVisitor());
        } catch (Exception exception) {
            throw new IllegalArgumentException("can't delete");
        }
    }

    public static void recursiveCopy(Path from, Path to) throws IllegalArgumentException {
        Path destination = getTargetPath(from, to);
        try {
            //System.out.println("copy from " + from.toString());
            //System.out.println("copy to " + destination.toString());
            Files.walkFileTree(from, new CopyVisitor(from, destination));
        } catch (Exception exception) {
            throw new IllegalArgumentException("can't copy");
        }
    }

    public static void recursiveMove(Path from, Path to) throws IllegalArgumentException {
        recursiveCopy(from, to);
        recursiveDelete(from);
    }
}
