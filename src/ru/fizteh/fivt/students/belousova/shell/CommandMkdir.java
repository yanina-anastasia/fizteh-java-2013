package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandMkdir implements Command {
    private static final String name = "mkdir";
    private ShellState state;

    public CommandMkdir(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        try {
            Scanner scanner = new Scanner(args);
            scanner.next();
            String dirName = scanner.next();
            File newDirectory = FileUtils.getFileFromString(dirName, state);

            boolean success = newDirectory.mkdirs();
            if (!success) {
                throw new IOException("cannot create '" + dirName + "': directory already exists");
            }
        } catch (NoSuchElementException e) {
            throw new IOException(name + ": missing file operand");
        } catch (IOException e) {
            throw new IOException(name + ": " + e.getMessage());
        }
    }
}
