package ru.fizteh.fivt.students.valentinbarishev.shell;

final class ShellDir implements ShellCommand {
    private String name = "dir";
    private int numberOfParameters = 1;

    private Context context;
    private String[] args;

    public ShellDir(final Context newContext) {
        context = newContext;
    }

    @Override
    public void run() {
        String[] content = context.getDirContent();
        for (int i = 0; i < content.length; ++i) {
            System.out.println(content[i]);
        }
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
