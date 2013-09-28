package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CommandMkdir implements Command {
    private static final String name = "mkdir";
    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        Scanner scanner = new Scanner(args);
        scanner.next();
        String dirName = scanner.nextLine().substring(1);
        File newDirectory = new File(dirName);

        if (!newDirectory.isAbsolute()) {
            newDirectory = new File(MainShell.currentDirectory, dirName);
        }

        try {
            boolean success = newDirectory.mkdirs();
            if (!success) {
                throw new IOException("cannot create '" + dirName + "': directory already exists");
            }
        } catch (IOException e) {
            throw new IOException(name + ": " + e.getMessage());
        }
    }
}
