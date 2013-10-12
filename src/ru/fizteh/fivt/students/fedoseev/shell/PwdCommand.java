package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.IOException;

public class PwdCommand extends AbstractCommand {
    public PwdCommand() {
        super("pwd", 0);
    }

    public void execute(String[] input, AbstractShell.ShellState state) throws IOException {
        System.out.println(state.getCurState());
    }
}
