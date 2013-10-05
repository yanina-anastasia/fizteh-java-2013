package ru.fizteh.fivt.students.anastasyev.shell;

import java.io.File;
import java.io.IOException;

public class MkdirCommand implements Commands {
    private static void mkdir(String dir) throws IOException {
        File newDir = new File(Shell.userDir.toPath().resolve(dir).toString());
        if (newDir.exists()) {
            System.err.println("mkdir: " + dir + " already exists");
            return;
        }
        if (!newDir.mkdir()) {
            throw new IOException();
        }
    }

    @Override
    public boolean exec(String[] command) {
        if (command.length != 2) {
            System.err.println("mkdir: Usage - mkdir <dirname>");
            return false;
        }
        try {
            mkdir(command[1]);
        } catch (Exception e) {
            System.err.println("mkdir: can't create " + command[1]);
            return false;
        }
        return true;
    }

    @Override
    public String commandName() {
        return "mkdir";
    }
}
