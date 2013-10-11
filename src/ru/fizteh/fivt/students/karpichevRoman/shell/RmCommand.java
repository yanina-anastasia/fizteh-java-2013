package ru.fizteh.fivt.students.karpichevRoman.shell;

import java.nio.file.Path;

class RmCommand implements Command {
    private static String name;

    public RmCommand() {
        name = "rm";
    }

    public boolean isThatCommand(String command) {
        return name.equals(command.trim().substring(0, Math.min(name.length(), command.length())));
    }

    public void run(Shell shell, String command) throws IllegalArgumentException {
        String[] splitedArray = command.trim().split("\\s+");
        
        if (splitedArray.length != 2) {
            throw new IllegalArgumentException("wrong number of arguments for rm " + command);
        }

        Path rmPath = shell.getCurrentPath().resolve(splitedArray[1]);
        
        Util.recursiveDelete(rmPath);
    }
}
