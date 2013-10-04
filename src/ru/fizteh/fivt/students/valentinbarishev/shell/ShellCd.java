package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

public class ShellCd implements ShellCommand {
    static String name = "cd";
    private Context context;
    private String[] args;

    public ShellCd(Context newContext) {
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
    public boolean isMyCommand(String[] command) {
        if (command[0].equals(name)) {
            if (command.length > 2) {
                throw new InvalidCommandException(name + " too many arguments");
            }
            if (command.length == 1) {
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
}
