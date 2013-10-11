package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

final class ShellMkdir implements ShellCommand {
    private String name = "mkdir";
    private int numberOfParameters = 2;

    private Context context;
    private String[] args;

    public ShellMkdir(final Context newContext) {
        context = newContext;
    }

    @Override
    public void run() {
        try {
            context.makeDir(args[1]);
        } catch (IOException e) {
            throw new InvalidCommandException(name + " argument " + args[1]);
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
                throw new InvalidCommandException("Usage: " + name + " <new dir>");
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
