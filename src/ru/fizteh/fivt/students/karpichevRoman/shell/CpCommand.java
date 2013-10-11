package ru.fizteh.fivt.students.karpichevRoman.shell;

import java.nio.file.Path;

class CpCommand implements Command {
    private static String name;

    public CpCommand() {
        name = "cp";
    }

    public boolean isThatCommand(String command) {
        return name.equals(command.trim().substring(0, Math.min(name.length(), command.length())));
    }

    public void run(Shell shell, String command) throws IllegalArgumentException {
        String[] splitedArray = command.trim().split("\\s+");
        
        if (splitedArray.length != 3) {
            throw new IllegalArgumentException("wrong number of arguments for cp " + command);
        }

        Path fromPath = shell.getCurrentPath().resolve(splitedArray[1]);
        Path toPath   = shell.getCurrentPath().resolve(splitedArray[2]);

        //System.out.println("cp from = " + fromPath.toString());
        //System.out.println("cp to   = " + toPath.toString());
                
        Util.recursiveCopy(fromPath, toPath);
    }
}
