package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class MkdirCommand extends ShellCommand {

    @Override
    public boolean execute(String args[]) {
        assert (args.length != 0);
        if (args.length != 2) {
            printMessage(args[0] + ": invalid number of arguments in the \'" + args[0] + "\' command");
            return false;
        }

        try {
            if (!(new File(currentPath.getAbsolutePath() + File.separator + args[1]).mkdir())) {
                printMessage(args[0] + ": \'" + args[1] + "\': couldn't create directory");
            }
            return true;
        } catch (SecurityException e) {
            printMessage(args[0] + ": \'" + args[1] + "\': couldn't create directory");
        }
        return false;
    }

    @Override
    public String getName() {
        return "mkdir";
    }
}
