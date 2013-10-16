package ru.fizteh.fivt.students.drozdowsky.shell.commands;

import ru.fizteh.fivt.students.drozdowsky.shell.PathController;
import java.io.File;
import java.io.IOException;

public class Directory {
    private PathController path;
    private String[] args;

    public Directory(PathController path, String[] args) {
        this.path = path;
        this.args = args;
    }

    public boolean execute() {
        try {
            PathController temp = new PathController(path);
            if (args.length > 1) {
                temp.changePath(args[1]);
            }
            File totalPath = temp.getPath();
            String[] result = totalPath.list();
            if (result == null) {
                return true;
            }

            for (String aResult : result) {
                System.out.println(aResult);
            }
        } catch (SecurityException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.err.println("cp: " + e.getMessage());
        }
        return true;
    }
}
