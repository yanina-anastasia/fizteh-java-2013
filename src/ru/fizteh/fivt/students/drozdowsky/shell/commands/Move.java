package ru.fizteh.fivt.students.drozdowsky.shell.commands;

import ru.fizteh.fivt.students.drozdowsky.shell.PathController;

import java.io.IOException;

public class Move {
    private PathController path;
    private String[] args;

    public Move(PathController path, String[] args) {
        this.path = path;
        this.args = args;
    }

    public boolean execute() {
        if (args.length != 3) {
            System.err.println("usage: mv file|directory file|directory");
            return false;
        }
        try {
            PathController pathFrom = new PathController(path);
            PathController pathTo = new PathController(path);
            pathFrom.changePath(args[1]);
            pathTo.changePath(args[2]);

            if (pathFrom.getPath().equals(pathTo.getPath())) {
                System.err.println(args[1] + " and " + args[2] + " are identical (not moved).");
                return false;
            }

            Copy cp = new Copy(path, args);
            if (cp.execute()) {
                String[] args2 = new String[2];
                args2[1] = args[1];
                args2[0] = args[0];
                Remove rm = new Remove(path, args2);
                return rm.execute();
            }
            return false;
        } catch (SecurityException e) {
            System.err.println("mv: " + "Permission denied");
        } catch (IOException e) {
            System.err.println("mv: " +  e.getMessage());
        }
        return false;
    }
}
