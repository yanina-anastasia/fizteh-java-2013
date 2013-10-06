package ru.fizteh.fivt.students.anastasyev.shell;

import java.io.File;
import java.io.IOException;

public class MkdirCommand implements Command {
    private static boolean mkdir(String dir) throws IOException {
        File newDir = new File(Shell.userDir.toPath().resolve(dir).toString());
        if (newDir.exists()) {
            System.err.println("mkdir: " + dir + " already exists");
            return false;
        }
        if (!newDir.mkdir()) {
            throw new IOException();
        }
        return true;
    }

    @Override
    public boolean exec(String[] command) {
        if (command.length != 2) {
            System.err.println("mkdir: Usage - mkdir <dirname>");
            return false;
        }
        boolean result = false;
        try {
            result = mkdir(command[1]);
        } catch (Exception e) {
            System.err.println("mkdir: can't create " + command[1]);
            return false;
        }
        return result;
    }

    @Override
    public String commandName() {
        return "mkdir";
    }
}
