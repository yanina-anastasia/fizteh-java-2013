package ru.fizteh.fivt.students.drozdowsky.shell.Commands;

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

    public void execute() {

        try {
            PathController temp = new PathController(path);
            if (args.length > 1) {
                temp.changePath(args[1]);
            }
            File totalPath = temp.getPath();
            String[] result = totalPath.list();
            if (result == null) {
                return;
            }

            for (int i = 0; i < result.length; i++) {
                System.out.println(result[i]);
            }
        } catch (SecurityException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.err.println("cp: " + e.getMessage());
        }
    }
}
