package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

final class ShellCp implements ShellCommand {
    private String name = "cp";
    private int numberOfParameters = 3;

    private Context context;
    private String[] args;

    public ShellCp(final Context newContext) {
        context = newContext;
    }

    @Override
    public void run() {
        try {
            context.copy(args[1], args[2]);
        } catch (IOException e) {
            throw new InvalidCommandException(name + " " + e.getMessage());
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
                        + "<src file/dir> <dest file/dir>");
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
