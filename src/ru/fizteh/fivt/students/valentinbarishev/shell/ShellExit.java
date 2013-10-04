package ru.fizteh.fivt.students.valentinbarishev.shell;

/**
 * Created with IntelliJ IDEA.
 * User: Valik
 * Date: 04.10.13
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */
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
