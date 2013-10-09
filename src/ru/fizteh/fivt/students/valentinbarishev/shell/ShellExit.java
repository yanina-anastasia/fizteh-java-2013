package ru.fizteh.fivt.students.valentinbarishev.shell;

final class ShellExit implements ShellCommand {
    private String name = "exit";
    private int numberOfParameters = 1;

    public ShellExit() {
    }

    @Override
    public void run() {
        System.exit(0);
    }

    @Override
    public boolean isMyCommand(final String[] command) {
        if (command[0].equals(name)) {
            if (command.length > numberOfParameters) {
                throw new InvalidCommandException(name
                        + " too many arguments!");
            }
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
