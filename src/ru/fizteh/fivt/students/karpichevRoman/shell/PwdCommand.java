package ru.fizteh.fivt.students.karpichevRoman.shell;

class PwdCommand implements Command {
    private static String name;

    public PwdCommand() {
        name = "pwd";
    }

    public boolean isThatCommand(String command) {
        return name.equals(command.trim());
    }

    public void run(Shell shell, String command) throws IllegalArgumentException {
        shell.echo(shell.getCurrentPath().toString());
    }
}
