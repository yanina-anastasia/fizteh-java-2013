package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandCd implements Command {
    private static final String name = "cd";

    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        try {
            Scanner scanner = new Scanner(args);
            scanner.next();
            String directory = scanner.nextLine();
            directory = directory.substring(1);
            File newDirectory = new File(directory);

            if (!newDirectory.isAbsolute()) {
                newDirectory = new File(MainShell.currentDirectory, directory);
            }

            if (newDirectory.exists() && newDirectory.isDirectory()) {
                MainShell.currentDirectory = newDirectory.getCanonicalFile();
            } else {
                throw new IOException("'" + directory + "': No such file or directory");
            }

        } catch (NoSuchElementException e) {
            throw new IOException(name + ": missing file operand");
        } catch (IOException e) {
            throw new IOException(name + ": " + e.getMessage());
        }

    }
}
