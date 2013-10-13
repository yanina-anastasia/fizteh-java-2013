package ru.fizteh.fivt.students.drozdowsky.shell.Commands;

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
            PathController test = new PathController(path);
            if (args.length == 0) {
                test.changePath("~");
            } else {
                test.changePath(args[1]);
            }

            if (test.isDirectory()) {
                if (args.length == 0) {
                    path.changePath("~");
                } else {
                    path.changePath(args[1]);
                }
            } else {
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
