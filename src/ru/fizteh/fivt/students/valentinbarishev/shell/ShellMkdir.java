package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Valik
 * Date: 03.10.13
 * Time: 2:40
 * To change this template use File | Settings | File Templates.
 */
public class ShellMkdir implements ShellCommand{
    static String name = "mkdir";

    private Context context;
    private String[] args;

    public ShellMkdir(Context newContext) {
        context = newContext;
    }

    @Override
    public void run() {
        try {
            context.makeDir(args[1]);
        } catch (IOException e) {
            throw new InvalidCommandException(name +" argument " + args[1]);
        }
    }

    @Override
    public boolean isMyCommand(String[] command) {
        if (command[0].equals(name)) {
            if (command.length > 2) {
                throw new InvalidCommandException(name + " too many arguments!");
            }
            if (command.length == 1) {
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
}
