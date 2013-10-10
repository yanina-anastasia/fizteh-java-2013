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
import java.lang.Exception;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Shell {
    private static File currentDirectory = new File(".");
    public static void main(String[] arg) {
        Pattern delCom = Pattern.compile("[ ;\n\t]");
        Pattern del = Pattern.compile("[ \n\t]");
        Scanner input = new Scanner(System.in);
        input.useDelimiter(delCom);
        System.out.print("$ ");
        String command = input.next();
        while (!command.equals("exit")) {
            if (command.equals("cd")) {
                changeDir(input.next());
            }
            if (command.equals("mkdir")) {
                try {
                    makeDir(input.next());
                } catch (Exception e) {
                    System.err.println("mkdir: " + e.toString());
                }
            }
            if (command.equals("pwd")) {
                printWorkingDir();
            }
            if (command.equals("rm")) {
                String argument = input.next();
                try {
                    remove(argument);
                } catch(IOException e) {
                   System.err.println("rm: " + e.toString());
                }
            }
            if (command.equals("cp")) {
                try {
                    copy(input.next(), input.next());
                } catch(Exception e) {
                    System.err.println("cp: " + e.toString());
                }
            }
            if (command.equals("dir")) {
                dir();
            }
            if (command.equals("mv")) {
                String arg1 = input.next();
                String arg2 = input.next();
                try {
                    move(arg1, arg2);
                } catch(Exception e) {
                    System.err.println("mv: " + e.toString());
                }
            }
            System.out.print("$ ");
            command = input.next();
        }
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

    private static void makeDir(String dirName) throws Exception{
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
        for(int i = 0; i < s.length; i++){
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
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                                throws IOException
                        {
                            Path targetDir = target.resolve(source.relativize(dir));
                            try {
                                Files.copy(dir, targetDir);
                            } catch (FileAlreadyExistsException e) {
                                if (!Files.isDirectory(targetDir))
                                    throw e;
                            }
                            return FileVisitResult.CONTINUE;
                        }
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                throws IOException
                        {
                            Files.copy(file, target.resolve(source.relativize(file)));
                            return FileVisitResult.CONTINUE;
                        }
                    });
    }

    static private void move(String source, String destination) throws Exception {
        File f1 = new File(source);
        File f2 = new File(destination);
        if (f1.toPath().toAbsolutePath().getParent().equals(f2.toPath().toAbsolutePath().getParent())) {
            if (!f1.renameTo(f2)) {
                throw new Exception ("An unexpected error");
            }
        }
        copy (source, destination);
        remove(source);
    }

}
