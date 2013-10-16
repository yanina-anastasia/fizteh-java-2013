package ru.fizteh.fivt.students.mikhaylova_daria.shell;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MyFileSystem {
    private static File currentDirectory = new File(".");

    public static void cd(String[] arg) throws Exception {
        String pathString = arg[1];
        File newDir = new File(pathString);
        if (!newDir.isAbsolute()) {
            newDir = currentDirectory.toPath().resolve(newDir.toPath()).toFile();
        }
        if (newDir.isDirectory()) {
            currentDirectory = newDir;
        } else {
            throw new Exception(pathString + "': It's not a directory");
        }
    }

    public static void md(String[] command) throws Exception {
        File newDir = new File(command[1]);
        if (!newDir.isAbsolute()) {
            newDir = currentDirectory.toPath().resolve(newDir.toPath()).normalize().toFile();
        }
        if (!newDir.mkdirs()) {
            throw new Exception("A directory with the same name already exists: " + newDir.getAbsolutePath());
        }
    }

    public static void pwd(String[] command) {
        System.out.println(currentDirectory.getAbsoluteFile().toPath().normalize().toString());
    }

    public static void dir(String[] command) {
        String[] s = currentDirectory.list();
        int i;
        for (i = 0; i < s.length; i++) {
            System.out.println(s[i]);
        }
    }

    public static void rm(String[] command) throws IOException {
        if (command[1].equals(".")) {
            command[1] = currentDirectory.toString();
        } else {
            if (command[1].equals("..")) {
                command[1] = currentDirectory.getParent();
            }
        }
        if (command[1].charAt(0) == '.') {
            command[1] = command[1].substring(2);
        }
        File name = new File(command[1]);
        if (!name.toPath().isAbsolute()) {
            name = currentDirectory.toPath().resolve(name.toPath()).normalize().toFile();
        }
        if (name.isFile()) {
            Files.delete(name.toPath());
        } else {
            Path start = name.toPath();
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e)
                        throws IOException {
                    if (e == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw e;
                    }
                }
            });
            if (!currentDirectory.toPath().normalize().toAbsolutePath().toFile().exists()) {
                currentDirectory = currentDirectory.toPath().getRoot().toFile();
            }

        }
    }

    public static void cp(String[] arg) throws Exception {
        String sourceStr = arg[1];
        String destination = arg[2];
        if (sourceStr.equals(".")) {
            sourceStr = currentDirectory.toPath().toAbsolutePath().normalize().toString();
        } else {
            if (sourceStr.equals("..")) {
                sourceStr = currentDirectory.toPath().normalize().toAbsolutePath().
                        getParent().toString();
            }
        }
        if (destination.equals(".")) {
            destination = currentDirectory.toPath().toAbsolutePath().normalize().toString();
        } else {
            if (destination.equals("..")) {
                destination = currentDirectory.toPath().normalize().toAbsolutePath().getParent().toString();
            }
        }
        File arg1 = new File(sourceStr);
        File arg2 = new File(destination);
        if (!arg1.isAbsolute()) {
            arg1 = currentDirectory.toPath().normalize().resolve(arg1.toPath()).normalize().toFile();
        }
        if (!arg2.isAbsolute()) {
            arg2 = currentDirectory.toPath().normalize().resolve(arg2.toPath()).normalize().toFile();
        }
        if (!arg1.toPath().toAbsolutePath().toFile().exists()) {
            throw new Exception("File source not found");
        }
        if (!arg2.toPath().toAbsolutePath().toFile().exists()) {
            Files.createFile(arg2.toPath().toAbsolutePath());
        }
        final Path source = arg1.toPath().toAbsolutePath();
        Path targetDir = arg2.toPath().toAbsolutePath();
        final Path target = arg2.toPath().toAbsolutePath();
        if (targetDir.startsWith(source)) {
            if (targetDir.equals(source)) {
                throw new Exception("Copying is not possible: this arguments are the same");
            }
            throw new Exception("Copying to a subfolder is impossible");
        }
        if (source.toFile().isFile() && target.toFile().isFile()) {
            Files.copy(source, target, REPLACE_EXISTING);
            return;
        }
        if (source.toFile().isFile() && target.toFile().isDirectory()) {
            Files.copy(source, target.resolve(source.getFileName()), REPLACE_EXISTING);
            return;
        }
        if (targetDir.toFile().isFile() && source.toFile().isDirectory()) {
            throw new Exception("Copying a directory to file");
        }
        if (!target.resolve(source.getFileName()).toFile().mkdirs()) {
            throw new Exception("A directory with the same name already exists in the target path\n");
        }
        final Path tar = target.resolve(source.getFileName());
        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path targetDir = tar.resolve(source.relativize(dir)).normalize();
                        try {
                            Files.copy(dir, targetDir);
                        } catch (FileAlreadyExistsException e) {
                            if (!Files.isDirectory(targetDir)) {
                                throw e;
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.copy(file, tar.resolve(source.relativize(file)));
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    public static void mv(String[] arg) throws Exception {
        String[] command = new String[] {"cp", arg[1], arg[2]};
        cp(command);
        String[] command1 = new String[] {"rm", arg[1]};
        rm(command1);
    }
}
