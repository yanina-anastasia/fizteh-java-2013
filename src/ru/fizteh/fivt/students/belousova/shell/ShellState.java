package ru.fizteh.fivt.students.belousova.shell;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShellState {

    private File currentDirectory;

    public ShellState() {
        currentDirectory = new File(".");
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String directory) throws IOException {
        File newDirectory = FileUtils.getFileFromString(directory, this);
        if (newDirectory.exists() && newDirectory.isDirectory()) {
            currentDirectory = newDirectory.getCanonicalFile();
        } else {
            throw new IOException("'" + directory + "': No such file or directory");
        }
    }
}
