package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

final class ShellMv implements ShellCommand {
    private String name = "mv";
    private int numberOfParameters = 3;

    private Context context;
    private String[] args;

    public ShellMv(final Context newContext) {
        context = newContext;
    }

    @Override
    public void run() {
        try {
            context.move(args[1], args[2]);
        } catch (IOException e) {
            throw new InvalidCommandException(name + " bad arguments "
                    + args[1] + " " + args[2]);
        }
    }

    @Override
    public boolean isMyCommand(final String[] command) {
        if (command[0].equals(name)) {
            if (command.length > numberOfParameters) {
                throw new InvalidCommandException(name
                        + " too many arguments!");
            }
            if (command.length < numberOfParameters) {
                throw new InvalidCommandException("Usage: " + name
                        + "<src> <dest>");
            }
            args = command;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumberOfParameters() {
        return numberOfParameters;
    }
}
