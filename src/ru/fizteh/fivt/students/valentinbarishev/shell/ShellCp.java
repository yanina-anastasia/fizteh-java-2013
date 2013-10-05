package ru.fizteh.fivt.students.valentinbarishev.shell;

import java.io.IOException;

public class ShellCp implements ShellCommand {
    static String name = "cp";

    private Context context;
    private String[] args;

    public ShellCp(Context newContext) {
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
    public boolean isMyCommand(String[] command) {
        if (command[0].equals(name)) {
            if (command.length > 3) {
                throw new InvalidCommandException(name + " too many arguments!");
            }
            if (command.length < 3) {
                throw new InvalidCommandException("Usage: " + name + "<src file/dir> <dest file/dir>");
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
