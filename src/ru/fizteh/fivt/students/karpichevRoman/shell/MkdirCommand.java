package ru.fizteh.fivt.students.karpichevRoman.shell;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

class MkdirCommand implements Command {
    private static String name;

    public MkdirCommand() {
        name = "mkdir";
    }

    public boolean isThatCommand(String command) {
        return name.equals(command.trim().substring(0, Math.min(name.length(), command.length())));
    }

    public void run(Shell shell, String command) throws IllegalArgumentException {
        String[] splitedArray = command.trim().split("\\s+");
        if (splitedArray.length != 2) {
            throw new IllegalArgumentException("wrong number of arguments for mkdir " + command);
        }

        Path dirPath = shell.getCurrentPath().resolve(splitedArray[1]);
        
        try {
            Files.createDirectory(dirPath);
        } catch (IOException exception) {
            throw new IllegalArgumentException("can't create directory"
            + "(haven't permissions, already exist or parent directory not exist)");
        }
    }
}
