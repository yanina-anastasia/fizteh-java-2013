package ru.fizteh.fivt.students.drozdowsky.shell.Commands;

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
        if (args.length < 3) {
            System.err.println("usage: mv file|directory file|directory");
            return false;
        }
        try {
            PathController pathFrom = new PathController(path);
            PathController pathTo = new PathController(path);
            pathFrom.changePath(args[1]);
            pathTo.changePath(args[2]);

            if (pathFrom.getPath().equals(pathTo.getPath())) {
                return true;
            }

            Copy cp = new Copy(path, args);
            if (cp.execute()) {
                Remove rm = new Remove(path, args);
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
