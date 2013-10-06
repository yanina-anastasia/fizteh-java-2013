package ru.fizteh.fivt.students.anastasyev.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MvCommand implements Command {
    private static void move(Path pathFrom, Path pathTo) throws IOException {
        File from = new File(Shell.userDir.toPath().resolve(pathFrom.toString()).toString());
        File to = new File(Shell.userDir.toPath().resolve(pathTo.toString()).toString());
        if (from.isFile() && to.isDirectory()) {
            Files.move(from.toPath(), Shell.userDir.toPath().resolve(to.toPath()).resolve(from.getName()));
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
        File from = new File(Shell.userDir.toPath().resolve(pathFrom.toString()).toString());
        File to = new File(Shell.userDir.toPath().resolve(pathTo.toString()).toString());
        if (!from.exists()) {
            throw new IOException(from + " there is not such file or directory");
        }
        if (from.isDirectory() && to.isFile()) {
            throw new IOException("can't copy directory to file");
        }
        if (from.isFile() && (to.isFile() || !to.exists())) {
            Files.move(from.toPath(), to.toPath());
        } else if (from.isFile() && to.isDirectory()) {
            Files.move(from.toPath(), Shell.userDir.toPath().resolve(to.toPath()).resolve(from.getName()));
        } else if (from.isDirectory() && (to.isDirectory() || !to.exists())) {
            if (to.toPath().startsWith(from.toPath())) {
                throw new IOException("can't copy directory to subdirectory");
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

    @Override
    public boolean exec(String[] command) {
        if (command.length != 3) {
            System.err.println("mv: Usage - mv <source> <destination>");
            return false;
        }
        try {
            File from = new File(Shell.userDir.toPath().resolve(command[1]).toString());
            File to = new File(Shell.userDir.toPath().resolve(command[2]).toString());
            if (from.equals(to)) {
                System.err.println("cp: Can't write directory or file to itself");
                return false;
            }
            mv(from.toPath(), to.toPath());
        } catch (FileAlreadyExistsException e) {
            System.err.println("mv: File already exists " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("mv: " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "mv";
    }
}
