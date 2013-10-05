package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;

public class CommandCp implements Command {
    private final String name = "cp";
    private ShellState state;

    public CommandCp(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public int getArgCount() {
        return 2;
    }

    public void execute(String[] args) throws IOException {

        String sourceName = args[1];
        String destinationName = args[2];

        File source = FileUtils.getFileFromString(sourceName, state);
        File destination = FileUtils.getFileFromString(destinationName, state);

        if (!source.exists()) {
            throw new IOException("cannot copy '" + source.getName() + "': No such file or directory");
        }
        if (destination.isFile() && source.isDirectory()) {
            throw new IOException("cannot overwrite non-directory '" + destination.getName()
                    + "' with directory '" + source.getName() + "'");
        }
        if (source.equals(destination)) {
            throw new IOException("you try to copy '" + source.getName() + "' to itself");
        }

        if (source.isFile()) {
            if (!destination.exists() || destination.isFile()) {
                FileUtils.copyFileToFile(source, destination);
            } else {
                FileUtils.copyFileToFolder(source, destination);
            }
        } else {
            FileUtils.copyFolderToFolder(source, destination);
        }
    }
}
