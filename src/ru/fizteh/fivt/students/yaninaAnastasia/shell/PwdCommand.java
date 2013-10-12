package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.ShellState;

import java.io.IOException;
import java.lang.System;

public class PwdCommand extends Command {
    public boolean exec(String[] args, ShellState curState) throws IOException {
        if (args.length != 0) {
            System.err.println("Invalid arguments");
            return false;
        }
        curState.printWorkDir();
        return true;
    }

    public String getCmd() {
        return "pwd";
    }
}