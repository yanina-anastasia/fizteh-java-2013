package ru.fizteh.fivt.students.drozdowsky.shell.commands;

import ru.fizteh.fivt.students.drozdowsky.shell.PathController;

import java.io.IOException;

public class ChangeDirectory {
    private PathController path;
    private String[] args;

    public ChangeDirectory(PathController path, String[] args) {
        this.path = path;
        this.args = args;
    }

    public boolean execute() {
        try {
            if (args.length != 2) {
                System.err.println("usage: cd <absolute path|relative path>");
                return false;
            }
            PathController test = new PathController(path);

            test.changePath(args[1]);

            if (test.isDirectory()) {
                path.changePath(args[1]);
            } else {
                if (!test.exists()) {
                    System.err.println("cd: " + args[1] + ": No such file or directory");
                    return false;
                }
                System.err.println("cd: " + args[1] + ": Not a directory");
                return false;
            }
            return true;
        } catch (IOException e) {
            System.err.println("cd: " + args[1] + ": " + e.getMessage());
        }
        return false;
    }
}
