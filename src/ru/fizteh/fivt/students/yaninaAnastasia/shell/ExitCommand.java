package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.ShellState;

import java.io.IOException;

public class ExitCommand extends Command {
    public boolean exec(String[] args, ShellState curState) throws IOException {
        if (args.length > 0) {
            System.out.println("Invalid number of arguments");
            return false;
        }
        System.exit(0);
        return true;
    }

    public String getCmd() {
        return "exit";
    }
}