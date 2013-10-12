package ru.fizteh.fivt.students.mikhaylova_daria.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Scanner;

public class Shell {

    private static File currentDirectory = new File(".");

    public static void main(String[] arg) {
        boolean flag = true;
        Scanner input = new Scanner(System.in);
        String[] commandString;
        String inputString;
        while (flag) {
            if (arg.length == 0) {
                System.out.print("$ ");
                inputString = input.nextLine();
                commandString = inputString.split("[;]");
                flag = manager(commandString, false);
            } else {
                pack(arg);
                flag = false;
            }
        }
    }

    private static boolean manager(String[] commandString, boolean pack) {
        int i;
        for (i = 0; i < commandString.length; ++i) {
            String[] command = commandString[i].trim().split("\\s+");
            if (command[0].equals("exit")) {
                return false;
            }
            if (command[0].equals("cd")) {
                if (command.length != 2) {
                    System.err.println("cd: Incorrect number of arguments");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    changeDir(command[1]);
                }
            }
            if (command[0].equals("mkdir")) {
                if (command.length != 2) {
                    System.err.println("mkdir: Incorrect number of arguments");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    try {
                        makeDir(command[1]);
                    } catch (Exception e) {
                        System.err.println("mkdir: " + e.getMessage());
                        if (pack) {
                            System.exit(1);
                        }
                    }
                }
            }
            if (command[0].equals("pwd")) {
                if (command.length != 1) {
                    System.err.println("pwd: Incorrect number of arguments");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    printWorkingDir();
                }
            }
            if (command[0].equals("rm")) {
                if (command.length != 2) {
                    System.err.println("rm: Incorrect number of arguments");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    try {
                        remove(command[1]);
                    } catch (IOException e) {
                        System.err.println("rm: " + e.getMessage());
                        if (pack) {
                            System.exit(1);
                        }
                    }
                }
            }
            if (command[0].equals("cp")) {
                if (command.length != 3) {
                    System.err.println("cp: Incorrect number of arguments");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    try {
                        copy(command[1], command[2]);
                    } catch (Exception e) {
                        System.err.println("cp: " + e.toString());
                        if (pack) {
                            System.exit(1);
                        }
                    }
                }
            }

            if (command[0].equals("dir")) {
                if (command.length != 1) {
                    System.err.println("dir: Incorrect number of arguments");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    dir();
                }
            }
            if (command[0].equals("mv")) {
                if (command.length != 3) {
                    System.err.println("mv: Incorrect number of arguments");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    try {
                        move(command[1], command[2]);
                    } catch (Exception e) {
                        System.err.println("mv: " + e.getMessage());
                        if (pack) {
                            System.exit(1);
                        }
                    }
                }
            }
            if (!(command[0].equals("cd") || command[0].equals("mkdir") || command[0].equals("pwd")
                    || command[0].equals("rm") || command[0].equals("cp")
                    || command[0].equals("mv") || command[0].equals("dir")
                    || command[0].equals("exit"))) {
                if (!command[0].isEmpty()) {
                    System.err.println(command[0] + ": An unknown command");
                    if (pack) {
                        System.exit(1);
                    }
                }
            }
        }
        return true;
    }

    private static void pack(String[] arg) {
        StringBuilder builderArg;
        builderArg = new StringBuilder();
        int i;
        for (i = 0; i < arg.length; ++i) {
            builderArg.append(arg[i]);
            builderArg.append(" ");
        }
        String[] command = builderArg.toString().split("[;]");
        manager(command, true);

    }

    private static void changeDir(String pathString) {
        File newDir = new File(pathString);
        if (!newDir.isAbsolute()) {
            newDir = currentDirectory.toPath().resolve(newDir.toPath()).toFile();
        }
        if (newDir.isDirectory()) {
            currentDirectory = newDir;
        } else {
            System.err.println("Shell: cd: '" + pathString + "': It's not a directory");
        }
    }

    private static void makeDir(String dirName) throws Exception {
        File newDir = new File(dirName);
        if (!newDir.isAbsolute()) {
            newDir = currentDirectory.toPath().resolve(newDir.toPath()).normalize().toFile();
        }
        if (!newDir.mkdirs()) {
            throw new Exception("A directory with the same name already exists: " + newDir.getAbsolutePath());
        }
    }

    private static void printWorkingDir() {
        System.out.println(currentDirectory.getAbsoluteFile().toPath().normalize().toString());
    }

    private static void dir() {
        String[] s = currentDirectory.list();
        int i;
        for (i = 0; i < s.length; i++) {
            System.out.println(s[i]);
        }
    }

    private static void remove(String fileOrDir) throws IOException {
        if (fileOrDir.equals(".")) {
            fileOrDir = currentDirectory.toString();
        } else {
            if (fileOrDir.equals("..")) {
                fileOrDir = currentDirectory.getParent();
            }
        }
        if (fileOrDir.charAt(0) == '.') {
            fileOrDir = fileOrDir.substring(2);
        }
        File name = new File(fileOrDir);
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

    private static void copy(String sourceStr, String destination) throws Exception {
        if (sourceStr.equals(".")) {
            sourceStr = currentDirectory.toString();
        } else {
            if (sourceStr.equals("..")) {
                sourceStr = currentDirectory.toPath().normalize().toAbsolutePath().
                        getParent().toString();
            }
        }
        if (destination.equals(".")) {
            destination = currentDirectory.toPath().normalize().toString();
        } else {
            if (destination.equals("..")) {
                destination = currentDirectory.toPath().normalize().toAbsolutePath().getParent().toString();
            }
        }
        File arg1 = new File(sourceStr);
        File arg2 = new File(destination);
        if (!arg1.isAbsolute()) {
            arg1 = currentDirectory.toPath().resolve(arg1.toPath()).normalize().toFile();
        }
        if (!arg2.isAbsolute()) {
            arg2 = currentDirectory.toPath().resolve(arg2.toPath()).normalize().toFile();
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
        if (targetDir.toFile().isFile() && source.toFile().isDirectory()) {
            throw new Exception("Copying a directory to file");
        }
        if (!targetDir.toFile().mkdirs()) {
            throw new Exception("A directory with the same name already exists in the target path\n");
        }
        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path targetDir = target.resolve(source.relativize(dir)).normalize();
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
                        Files.copy(file, target.resolve(source.relativize(file)));
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    private static void move(String source, String destination) throws Exception {
        File f1 = new File(source);
        File f2 = new File(destination);
        if (!f1.isAbsolute()) {
            f1 = currentDirectory.toPath().resolve(f1.toPath()).normalize().toFile();
        }
        if (!f1.toPath().normalize().toAbsolutePath().toFile().exists()) {
            throw new Exception(f1.getName() + " not found");
        }
        if (!f2.isAbsolute()) {
            f2 = currentDirectory.toPath().resolve(f2.toPath()).normalize().toFile();
        }
        if (f1.toPath().normalize().toAbsolutePath().equals(f2.toPath().normalize().toAbsolutePath())) {
            throw new Exception("Copying is not possible: this arguments are the same");
        }
        if (f1.toPath().normalize().toAbsolutePath().getParent().
                equals(f2.toPath().normalize().toAbsolutePath().getParent())) {
            remove(destination);
            if (!f1.renameTo(f2.getAbsoluteFile())) {
                throw new Exception("An unexpected error");
            }
        } else {
            copy(source, destination);
            remove(source);
        }
    }
}
