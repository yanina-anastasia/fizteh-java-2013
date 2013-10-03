package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandCd implements Command {
    private final String name = "cd";
    private ShellState state;

    public CommandCd(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        try {
            Scanner scanner = new Scanner(args);
            scanner.next();
            String directory = scanner.next();
            state.setCurrentDirectory(directory);
        } catch (NoSuchElementException e) {
            throw new IOException(name + ": missing file operand");
        } catch (IOException e) {
            throw new IOException(name + ": " + e.getMessage());
        }

    }
}
