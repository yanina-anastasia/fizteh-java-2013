package ru.fizteh.fivt.students.karpichevRoman.shell;

import java.nio.file.Files;
import java.nio.file.Path;

class CdCommand implements Command {
    private static String name;

    public CdCommand() {
        name = "cd";
    }

    public boolean isThatCommand(String command) {
        return name.equals(command.trim().substring(0, Math.min(name.length(), command.length())));
    }

    public void run(Shell shell, String command) throws IllegalArgumentException {
        String[] splitedArray = command.trim().split("\\s+");
        
        if (splitedArray.length != 2) {
            throw new IllegalArgumentException("wrong number of arguments for cd command: " + command);
        }
        
        Path newPath = shell.getCurrentPath().resolve(splitedArray[1]).toAbsolutePath().normalize();
        
        try {
            if (!Files.isDirectory(newPath)) {
                throw new IllegalArgumentException("cd parameter is not directory");
            }
        } catch (SecurityException exception) {
            throw new IllegalArgumentException("haven't permissions to cd into directory");
        }

        shell.setCurrentPath(newPath);
    }
}
