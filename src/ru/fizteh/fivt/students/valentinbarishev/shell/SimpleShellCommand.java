package ru.fizteh.fivt.students.valentinbarishev.shell;

public abstract class SimpleShellCommand implements ShellCommand {
    private String name;
    private int numberOfArgs;
    private String[] args;
    private String hint;

    @Override
    public void run() {
    }

    @Override
    public final boolean isMyCommand(final String[] command) {
        if (name.equals(command[0])) {
            if (command.length > numberOfArgs) {
                throw new InvalidCommandException(name + ": too many arguments");
            }
            if (command.length < numberOfArgs) {
                throw new InvalidCommandException(name + " " + hint);
            }
            args = command;
            return true;
        }
        return false;
    }

    public final String getHint() {
        return hint;
    }

    public final void setHint(String newHint) {
        hint = newHint;
    }

    public final String getName() {
        return name;
    }

    public final int getNumberOfArgs() {
        return numberOfArgs;
    }

    public void setName(final String newName) {
        name = newName;
    }

    public void setNumberOfArgs(final int newNumberOfArgs) {
        numberOfArgs = newNumberOfArgs;
    }

    public String getArg(final int index) {
        return args[index];
    }
}
