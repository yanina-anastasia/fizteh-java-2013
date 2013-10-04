package ru.fizteh.fivt.students.valentinbarishev.shell;

public class ShellExit implements ShellCommand {
    static String name = "exit";

    public ShellExit() {
    }

    @Override
    public void run() {
        System.exit(0);
    }

    @Override
    public boolean isMyCommand(String[] command) {
        if (command[0].equals(name)) {
            if (command.length > 1) {
                throw new InvalidCommandException(name + " too many arguments!");
            }
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }
}
