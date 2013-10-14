package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import java.io.*;

public abstract class Command {
    Command() {

    }

    public abstract boolean exec(String[] args, ShellState curState) throws IOException;

    public abstract String getCmd();
}
