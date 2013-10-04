package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.File;

public class CpCommand extends ShellCommand {

    @Override
    public boolean execute(String args[]) {
        assert (args.length != 0);
        if (args.length != 3) {
            printMessage(args[0] + ": invalid number of arguments in the \'" + args[0] + "\' command");
            return false;
        }
        File source = resolvePath(args[1]);
        File destination = resolvePath(args[2]);
        return safeCopy(source, destination, getName());
    }

    @Override
    public String getName() {
        return "cp";
    }
}