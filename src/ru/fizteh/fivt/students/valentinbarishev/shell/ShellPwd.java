package ru.fizteh.fivt.students.valentinbarishev.shell;

final class ShellPwd implements ShellCommand {
    private String name = "pwd";
    private int numberOfParameters = 1;

    private Context context;
    private String[] args;

    public ShellPwd(final Context newContext) {
        context = newContext;
    }

    @Override
    public void run() {
        System.out.println(context.getCurrentDir());
    }

    @Override
    public boolean isMyCommand(final String[] command) {
        if (command[0].equals(name)) {
            if (command.length > numberOfParameters) {
                throw new InvalidCommandException(name
                        + " too many arguments!");
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
