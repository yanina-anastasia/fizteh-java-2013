package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandRm implements Command {
    private final String name = "rm";
    private ShellState state;

    public CommandRm(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void execute(String args) throws IOException {
        try {
            Scanner scanner = new Scanner(args);
            scanner.next();
            //String dirName = scanner.nextLine().substring(1);
            String dirName = scanner.next();
            File directory = FileUtils.getFileFromString(dirName, state);

            if (directory.equals(state.getCurrentDirectory())) {
                throw new IOException("cannot remove '" + directory.getName() + "': it is a working directory");
            }

            if (directory.exists()) {
                FileUtils.deleteDirectory(directory);
            } else {
                throw new IOException("cannot remove " + dirName + ": No such file or directory");
            }
        } catch (NoSuchElementException e) {
            throw new IOException(name + ": missing file operand");
        } catch (IOException e) {
            throw new IOException(name + ": " + e.getMessage());
        }
    }
}
