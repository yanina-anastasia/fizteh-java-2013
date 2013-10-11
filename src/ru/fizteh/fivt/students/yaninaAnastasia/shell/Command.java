package ru.fizteh.fivt.students.yaninaAnastasia.shell.Commands;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.ShellState;

import java.io.*;
import java.util.HashMap;

public abstract class Command {
    Command() {

    }

    public abstract boolean exec(String[] args, ShellState curState) throws IOException;

    public abstract String getCmd();
}