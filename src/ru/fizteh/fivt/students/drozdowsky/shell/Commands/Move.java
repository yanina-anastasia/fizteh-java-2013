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
            System.err.println("usage: rm file|directory file|directory");
            return false;
        }
        try {
            Copy cp = new Copy(new PathController(), args);
            if (cp.execute()) {
                Remove rm = new Remove(new PathController(), args);
                rm.execute();
            }
            return true;
        } catch (SecurityException e) {
            System.err.println("cp: " + "Permission denied");
        }
        return false;
    }
}
