package ru.fizteh.fivt.students.kochetovnicolai.shell;

public class PwdCommand extends ShellCommand {

    @Override
    public boolean execute(String[] args) {
        assert (args.length != 0);
        if (args.length > 1) {
            printMessage(args[0] + ": invalid number of arguments in the \'" + args[0] + "\' command");
        } else {
            printMessage(currentPath.getAbsolutePath());
            return true;
        }
        return false;
    }

    @Override
    public final String getName() {
        return "pwd";
    }
}
