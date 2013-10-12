package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

final class ShellCd implements ShellCommand {
    private String name = "cd";
    private int numberOfParameters = 2;

    private Context context;
    private String[] args;

    public ShellCd(final Context newContext) {
        context = newContext;
    }

    @Override
    public void run() {
        try {
            context.changeDir(args[1]);
        } catch (IOException e) {
            throw new InvalidCommandException(name + " argument: " + args[1] + " " + e.getMessage());
        }
    }

    @Override
    public boolean isMyCommand(final String[] command) {
        if (command[0].equals(name)) {
            if (command.length > numberOfParameters) {
                throw new InvalidCommandException(name + " too many arguments");
            }
            if (command.length < numberOfParameters) {
                throw new InvalidCommandException("Usage: " + name + "<absolute/relative path>");
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
