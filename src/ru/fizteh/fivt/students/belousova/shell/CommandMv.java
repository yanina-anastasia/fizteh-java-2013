package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandMv implements Command {
    private static final String name = "mv";

    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        Scanner scanner = new Scanner(args);
        try {
            scanner.next();
            String srcName = scanner.next();
            String dstName = scanner.next();

            File source = new File(srcName);
            File destination = new File(dstName);

            if (!source.isAbsolute()) {
                source = new File(MainShell.currentDirectory, srcName);
            }
            if (!destination.isAbsolute()) {
                destination = new File(MainShell.currentDirectory, dstName);
            }

            if (!source.exists()) {
                throw new IOException("cannot move '" + source.getName() + "': No such file or directory");
            }
            if (destination.isFile() && source.isDirectory()) {
                throw new IOException("cannot overwrite non-directory '" + destination.getName()
                        + "' with directory '" + source.getName() + "'");
            }

            if (!destination.exists() || destination.isFile()) {
                renameFile(source, destination);
            } else if (destination.isDirectory()) {
                moveToFolder(source, destination);
            }
        } catch (NoSuchElementException e) {
            throw new IOException(name + ": missing file operand");
        } catch (IOException e) {
            throw new IOException(name + ": " + e.getMessage());
        }
    }

    private static void renameFile(File oldFile, File newFile) throws IOException {
        if (newFile.equals(oldFile)) {
            throw new IOException("cannot move '" + oldFile.getName() + "' to itself");
        }
        if (newFile.exists()) {
            boolean successDelete = newFile.delete();
            if (!successDelete) {
                throw new IOException("cannot overwrite");
            }
        }

        File parent = new File(newFile.getParent());

        if (parent.exists() && !parent.equals(oldFile)) {
            boolean successRename = oldFile.renameTo(newFile);
            if (!successRename) {
                throw new IOException("cannot rename");
            }
        } else {
            String newName = parent.getName() + newFile.getName();
            File newNewFile = new File(parent.getParent() + File.separator + newName);
            renameFile(oldFile, newNewFile);
        }
    }

    private static void moveToFolder(File source, File destination) throws IOException {
        if (source.isFile()) {
            CopyFunctions.copyFileToFolder(source, destination);
        }
        if (source.isDirectory()) {
            CopyFunctions.copyFolderToFolder(source, destination);
        }
        DeleteFunctions.deleteDirectory(source);
    }
}
