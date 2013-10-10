/**
 * Created with IntelliJ IDEA.
 * User: darya
 * Date: 07.10.13
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */
package ru.fizteh.fivt.students.mikhaylova_daria.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Scanner;

public class Shell {

    private static File currentDirectory = new File(".");

    public static void main(String[] arg) {
        boolean flag = true;
        Scanner input = new Scanner(System.in);
        String[] commandString = arg;
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

    private static boolean manager (String[] commandString, boolean pack) {
        int i;
        for (i = 0; i < commandString.length; ++i) {
            String[] command = commandString[i].trim().split("\\s+");
            if (command[0].equals("exit")) {
                return false;
            }
            if (command[0].equals("cd")) {
                if (command.length != 2) {
                    System.err.println("An unknown command");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    changeDir(command[1]);
                }
            }
            if (command[0].equals("mkdir")) {
                if (command.length != 2) {
                    System.err.println("An unknown command");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    try {
                        makeDir(command[1]);
                    } catch (Exception e) {
                        System.err.println("mkdir: " + e.toString());
                        if (pack) {
                            System.exit(1);
                        }
                    }
                }
            }
            if (command[0].equals("pwd")) {
                if (command.length != 1) {
                    System.err.println("An unknown command");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    printWorkingDir();
                }
            }
            if (command[0].equals("rm")) {
                if (command.length != 2) {
                    System.err.println("An unknown command");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    try {
                        remove(command[1]);
                    } catch (IOException e) {
                        System.err.println("rm: " + e.toString());
                        if (pack) {
                            System.exit(1);
                        }
                    }
                }
            }
            if (command[0].equals("cp")) {
                if (command.length != 3) {
                    System.err.println("An unknown command");
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
                    System.err.println("An unknown command");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    dir();
                }
            }

            if (command[0].equals("mv")) {
                if (command.length != 3) {
                    System.err.println("An unknown command");
                    if (pack) {
                        System.exit(1);
                    }
                } else {
                    try {
                        move(command[1], command[2]);
                    } catch (Exception e) {
                        System.err.println("mv: " + e.toString());
                        if (pack) {
                            System.exit(1);
                        }
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
        String [] s = currentDirectory.list();
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
            if (!currentDirectory.exists()) {
                currentDirectory = currentDirectory.toPath().getRoot().toFile();
            }

        }
    }

    private static void copy(String sourceStr, String destination) throws Exception {
        if (sourceStr.equals(".")) {
            sourceStr = currentDirectory.toString();
        } else {
            if (sourceStr.equals("..")) {
                sourceStr = currentDirectory.getParent();
            }
        }
        if (destination.equals(".")) {
            destination = currentDirectory.toString();
        } else {
            if (destination.equals("..")) {
                destination = currentDirectory.getParent();
            }
        }
        File arg1 = new File(sourceStr);
        File arg2 = new File(destination);
        if (!arg1.isAbsolute()) {
            arg1 = currentDirectory.toPath().resolve(arg1.toPath()).toFile();
        }
        if (!arg2.isAbsolute()) {
            arg2 = currentDirectory.toPath().resolve(arg2.toPath()).toFile();
        }
        final Path source = arg1.toPath();
        Path targetDir = arg2.toPath();
        if (targetDir.startsWith(source)) {
            throw new Exception("Copying to a subfolder is impossible");
        }
            targetDir = targetDir.resolve(source.getFileName());
            if (!targetDir.toFile().mkdirs()) {
                throw new Exception("A directory with the same name already exists in the target path\n");
            }
            final Path target = targetDir;
            Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            Path targetDir = target.resolve(source.relativize(dir));
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
        if (f1.toPath().toAbsolutePath().getParent().equals(f2.toPath().toAbsolutePath().getParent())) {
            if (!f1.renameTo(f2)) {
                throw new Exception("An unexpected error");
            }
        }
        copy(source, destination);
        remove(source);
    }
}
