package ru.fizteh.fivt.students.belousova.shell;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandCp implements Command {
    private static final String name = "cp";

    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        try {
            Scanner scanner = new Scanner(args);
            scanner.next();
            String sourceName = scanner.next();
            String destinationName = scanner.next();

            File source = new File(sourceName);
            File destination = new File(destinationName);
            if (!source.isAbsolute()) {
                source = new File(MainShell.currentDirectory, sourceName);
            }
            if (!destination.isAbsolute()) {
                destination = new File(MainShell.currentDirectory, destinationName);
            }

            if (!source.exists()) {
                throw new IOException("cannot copy '" + source.getName() + "': No such file or directory");
            }
            if (destination.isFile() && source.isDirectory()) {
                throw new IOException("cannot overwrite non-directory '" + destination.getName()
                        + "' with directory '" + source.getName() + "'");
            }

            if (!destination.exists() || destination.isFile()) {
                CopyFunctions.copyFileToFile(source, destination);
            } else if (destination.isDirectory()) {
                if (source.isFile()) {
                    CopyFunctions.copyFileToFolder(source, destination);
                } else {
                    CopyFunctions.copyFolderToFolder(source, destination);
                }
            }

        } catch (NoSuchElementException e) {
            throw new IOException(name + ": missing file operand");
        } catch (IOException e) {
            throw new IOException(name + ": " + e.getMessage());
        }
    }
}
