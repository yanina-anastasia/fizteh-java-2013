package ru.fizteh.fivt.students.drozdowsky.shell.commands;

import ru.fizteh.fivt.students.drozdowsky.shell.PathController;
import java.io.File;
import java.io.IOException;

public class MakeDirectory {
    private PathController path;
    private String[] args;

    public MakeDirectory(PathController path, String[] args) {
        this.path = path;
        this.args = args;
    }

    public boolean execute() {
        if (args.length < 2) {
            System.err.println("usage: mkdir directory");
            return false;
        }
        try {
            PathController tempPath = new PathController(path);
            tempPath.changePath(args[1]);
            File newDirectory = tempPath.getPath();

            if (!newDirectory.mkdir() && newDirectory.exists()) {
                System.err.println("mkdir: " + args[1] + ": " + "File exists");
                return false;
            }
            return true;
        } catch (SecurityException e) {
            System.err.println("mkdir: " + args[1] + ": " + "Permission denied");
        } catch (IOException e) {
            System.err.println("mkdir: " + args[1] + ": " + e.getMessage());
        }
        return false;
    }
}
