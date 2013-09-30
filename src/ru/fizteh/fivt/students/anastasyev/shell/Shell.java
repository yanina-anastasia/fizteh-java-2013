package ru.fizteh.fivt.students.anastasyev.shell;

import java.lang.String;
import java.lang.StringBuilder;
import java.io.IOException;
import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Shell {
    private static File userDir;

    private static void cd(String dir) throws IOException {
        File newUserDir = new File(dir);
        if (!newUserDir.isAbsolute()) {
            newUserDir = new File(userDir.getAbsoluteFile().toPath().resolve(dir).toString());
        }
        if (!newUserDir.isDirectory()) {
            throw new IOException(dir + " directory doesn't exist");
        }
        userDir = newUserDir;
    }

    private static void mkdir(String dir) throws IOException {
        File newDir = new File(userDir.toPath().resolve(dir).toString());
        if (newDir.exists()) {
            System.err.println("mkdir: " + dir + " already exists");
            return;
        }
        if (!newDir.mkdir()) {
            throw new IOException();
        }
    }

    private static void pwd() {
        System.out.println(userDir.toPath().normalize());
    }

    private static void rm(Path removing) throws IOException {
        File remove = new File(removing.toString());
        if (!remove.exists()) {
            throw new IOException(removing + " there is not such file or directory");
        }
        if (remove.isFile()) {
            if (!remove.delete()) {
                throw new IOException(removing + " can't remove this file");
            }
        }
        if (remove.isDirectory()) {
            String[] fileList = remove.list();
            for (String files : fileList) {
                rm(removing.resolve(files));
            }
            if (!remove.delete()) {
                throw new IOException(removing + " can't remove this directory");
            }
        }
    }

    private static void copy(Path pathFrom, Path pathTo) throws IOException {
        File from = new File(userDir.toPath().resolve(pathFrom.toString()).toString());
        File to = new File(userDir.toPath().resolve(pathTo.toString()).toString());
        if (from.isFile() && to.isDirectory()) {
            Files.copy(from.toPath(), userDir.toPath().resolve(to.toPath()).resolve(from.getName()));
        } else if (from.isDirectory() && (to.isDirectory() || !to.exists())) {
            if (to.toPath().startsWith(from.toPath())) {
                throw new IOException("Can't copy directory to subdirectory");
            }
            if (!to.exists()) {
                to.mkdir();
            }
            File newDir = new File(to.toPath().resolve(from.getName()).toString());
            newDir.mkdir();
            File[] fromFiles = from.listFiles();
            for (File files : fromFiles) {
                copy(files.getAbsoluteFile().toPath(), to.getAbsoluteFile().toPath().resolve(from.getName()));
            }
        } else {
            throw new IOException("Incorrect file names");
        }
    }

    private static void cp(Path pathFrom, Path pathTo) throws IOException {
        File from = new File(userDir.toPath().resolve(pathFrom.toString()).toString());
        File to = new File(userDir.toPath().resolve(pathTo.toString()).toString());
        if (!from.exists()) {
            throw new IOException(from + " there is not such file or directory");
        }
        if (from.isDirectory() && to.isFile()) {
            throw new IOException("can't copy directory to file");
        }
        if (from.isFile() && (to.isFile() || !to.exists())) {
            Files.copy(from.toPath(), to.toPath());
        } else if (from.isFile() && to.isDirectory()) {
            Files.copy(from.toPath(), userDir.toPath().resolve(to.toPath()).resolve(from.getName()));
        } else if (from.isDirectory() && (to.isDirectory() || !to.exists())) {
            if (to.toPath().startsWith(from.toPath())) {
                throw new IOException("Can't copy directory to subdirectory");
            }
            if (!to.exists()) {
                to.mkdir();
            }
            File[] fromFiles = from.listFiles();
            for (File files : fromFiles) {
                copy(files.getAbsoluteFile().toPath(), to.getAbsoluteFile().toPath());
            }
        } else {
            throw new IOException("Incorrect file names");
        }
    }

    private static void move(Path pathFrom, Path pathTo) throws IOException {
        File from = new File(userDir.toPath().resolve(pathFrom.toString()).toString());
        File to = new File(userDir.toPath().resolve(pathTo.toString()).toString());
        if (from.isFile() && to.isDirectory()) {
            Files.move(from.toPath(), userDir.toPath().resolve(to.toPath()).resolve(from.getName()));
        } else if (from.isDirectory() && (to.isDirectory() || !to.exists())) {
            if (to.toPath().startsWith(from.toPath())) {
                throw new IOException("Can't copy directory to subdirectory");
            }
            if (!to.exists()) {
                to.mkdir();
            }
            File newDir = new File(to.toPath().resolve(from.getName()).toString());
            newDir.mkdir();
            File[] fromFiles = from.listFiles();
            for (File files : fromFiles) {
                move(files.getAbsoluteFile().toPath(), to.getAbsoluteFile().toPath().resolve(from.getName()));
            }
        } else {
            throw new IOException("Incorrect file names");
        }
        from.delete();
    }

    private static void mv(Path pathFrom, Path pathTo) throws IOException {
        File from = new File(userDir.toPath().resolve(pathFrom.toString()).toString());
        File to = new File(userDir.toPath().resolve(pathTo.toString()).toString());
        if (!from.exists()) {
            throw new IOException(from + " there is not such file or directory");
        }
        if (from.isDirectory() && to.isFile()) {
            throw new IOException("can't copy directory to file");
        }
        if (from.isFile() && (to.isFile() || !to.exists())) {
            Files.move(from.toPath(), to.toPath());
        } else if (from.isFile() && to.isDirectory()) {
            Files.move(from.toPath(), userDir.toPath().resolve(to.toPath()).resolve(from.getName()));
        } else if (from.isDirectory() && (to.isDirectory() || !to.exists())) {
            if (to.toPath().startsWith(from.toPath())) {
                throw new IOException("Can't copy directory to subdirectory");
            }
            if (!to.exists()) {
                to.mkdir();
            }
            File[] fromFiles = from.listFiles();
            for (File files : fromFiles) {
                move(files.getAbsoluteFile().toPath(), to.getAbsoluteFile().toPath());
            }
            from.delete();
        } else {
            throw new IOException("Incorrect file names");
        }
    }

    private static void dir() {
        String[] fileList = userDir.list();
        for (String files : fileList) {
            System.out.println(files);
        }
    }

    private static void launcher(String arg) throws IOException {
        String[] command = arg.split(" ");
        if (command[0].equals("cd") && command.length == 2) {
            try {
                cd(command[1]);
            } catch (IOException e) {
                System.err.println("cd: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("cd: can't change directory");
            }
        } else if (command[0].equals("mkdir") && command.length == 2) {
            try {
                mkdir(command[1]);
            } catch (Exception e) {
                System.err.println("mkdir: can't create " + command[1]);
            }
        } else if (command[0].equals("pwd") && command.length == 1) {
            try {
                pwd();
            } catch (Exception e) {
                System.err.println("pwd: wrong path");
            }
        } else if (command[0].equals("rm") && command.length == 2) {
            try {
                File removing = new File(userDir.toPath().resolve(command[1]).toString());
                rm(removing.toPath());
            } catch (IOException e) {
                System.err.println("rm: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("rm: can't remove " + command[1]);
            }
        } else if (command[0].equals("cp") && command.length == 3) {
            try {
                File from = new File(userDir.toPath().resolve(command[1]).toString());
                File to = new File(userDir.toPath().resolve(command[2]).toString());
                cp(from.toPath(), to.toPath());
            } catch (FileAlreadyExistsException e) {
                System.err.println("cp: File already exists " + e.getMessage());
            } catch (Exception e) {
                System.err.println("cp: " + e.getMessage());
            }

        } else if (command[0].equals("mv") && command.length == 3) {
            try {
                File from = new File(userDir.toPath().resolve(command[1]).toString());
                File to = new File(userDir.toPath().resolve(command[2]).toString());
                mv(from.toPath(), to.toPath());
            } catch (FileAlreadyExistsException e) {
                System.err.println("mv: File already exists " + e.getMessage());
            } catch (Exception e) {
                System.err.println("mv: " + e.getMessage());

            }
        } else if (command[0].equals("dir") && command.length == 1) {
            try {
                dir();
            } catch (Exception e) {
                System.err.println("dir: wrong path");
            }
        } else if (command[0].equals("exit") && command.length == 1) {
            System.exit(0);
        } else {
            System.err.println("Wrong command");
        }
    }

    private static void interactiveMode() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.err.flush();
            System.out.print(userDir.toPath().normalize() + "$ ");
            try {
                String arg = scan.nextLine().trim();
                if (arg.equals("exit")) {
                    System.exit(0);
                }
                launcher(arg);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        userDir = new File(System.getProperty("user.dir"));
        if (args.length == 0) {
            interactiveMode();
        }
        StringBuilder allCommands = new StringBuilder();
        for (String arg : args) {
            allCommands.append(arg).append(" ");
        }
        String commands = allCommands.toString();
        String[] allArgs = commands.split(";");
        try {
            for (String arg : allArgs) {
                launcher(arg.trim());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}