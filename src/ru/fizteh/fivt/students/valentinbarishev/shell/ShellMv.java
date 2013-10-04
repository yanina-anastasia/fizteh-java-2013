package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Valik
 * Date: 04.10.13
 * Time: 19:42
 * To change this template use File | Settings | File Templates.
 */
public class ShellMv implements ShellCommand {
    static String name = "mv";
    private Context context;
    private String[] args;

    public ShellMv(Context newContext) {
        context = newContext;
    }

    @Override
    public void run() {
        try {
            context.move(args[1], args[2]);
        } catch (IOException e) {
            throw new InvalidCommandException(name + " bad arguments " + args[1] + " " + args[2]);
        }
    }

    @Override
    public boolean isMyCommand(String[] command) {
        if (command[0].equals(name)) {
            if (command.length > 3) {
                throw new InvalidCommandException(name + " too many arguments!");
            }
            if (command.length < 3) {
                throw new InvalidCommandException("Usage: " + name + "<src> <dest>");
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
}
