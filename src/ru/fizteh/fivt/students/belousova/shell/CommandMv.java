package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;

public class CommandMv implements Command {
    private final String name = "mv";
    private ShellState state;

    public CommandMv(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public int getArgCount() {
        return 2;
    }

    public void execute(String[] args) throws IOException {

        String srcName = args[1];
        String dstName = args[2];

        File source = FileUtils.getFileFromString(srcName, state);
        File destination = FileUtils.getFileFromString(dstName, state);

        if (!source.exists()) {
            throw new IOException("cannot move '" + source.getName() + "': No such file or directory");
        }
        if (destination.isFile() && source.isDirectory()) {
            throw new IOException("cannot overwrite non-directory '" + destination.getName()
                    + "' with directory '" + source.getName() + "'");
        }
        if (source.equals(state.getCurrentDirectory())) {
            throw new IOException("cannot move '" + source.getName() + "': it is a working directory");
        }
        if (source.equals(destination)) {
            throw new IOException("you try to move '" + source.getName() + "' to itself");
        }

        if (!destination.exists() || destination.isFile()) {
            FileUtils.renameFile(source, destination);
        } else if (destination.isDirectory()) {
            FileUtils.moveToFolder(source, destination);
        }
    }
}
