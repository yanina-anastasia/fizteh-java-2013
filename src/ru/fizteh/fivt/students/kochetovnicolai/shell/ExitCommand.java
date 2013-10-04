package ru.fizteh.fivt.students.kochetovnicolai.shell;

public class ExitCommand extends ShellCommand {

    @Override
    public boolean execute(String args[]) {
        assert (args.length != 0);
        if (args.length > 1) {
            printMessage(args[0] + ": invalid number of arguments in the \'" + args[0] + "\' command");
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "exit";
    }
}
