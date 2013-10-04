package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class CdCommand extends ShellCommand {

    @Override
    public boolean execute(String args[]) {
        assert (args.length != 0);
        if (args.length != 2) {
            printMessage(args[0] + ": invalid number of arguments in the \'" + args[0] + "\' command");
            return false;
        }
        File newPath = resolvePath(args[1]);
        if (newPath == null || !newPath.exists()) {
            printMessage(args[0] + ": \'" + args[1] + "\': No such file or directory");
        } else if (!newPath.isDirectory()) {
            printMessage(args[0] + ": \'" + args[1] + "': expected directory name, but file found");
        } else {
            currentPath = newPath.getAbsoluteFile();
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "cd";
    }
}
