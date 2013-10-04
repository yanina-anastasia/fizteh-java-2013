package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class RmCommand extends ShellCommand {

    @Override
    public boolean execute(String args[]) {
        assert (args.length != 0 && args[0].equals("rm"));
        if (args.length != 2) {
            printMessage(args[0] + ": invalid number of arguments in the \'" + args[0] + "\' command");
            return false;
        }

        File files[] = currentPath.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(args[1])) {
                    return recursiveRemove(file, args[0]);
                }
            }
        }
        printMessage(args[0] + ": cannot remove \'" + args[1] + "\': No such file or directory");
        return false;
    }

    @Override
    public String getName() {
        return "rm";
    }
}
