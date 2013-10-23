package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class PwdCommand extends AbstractCommand<ShellState> {
    public PwdCommand() {
        super("pwd", 0);
    }

    @Override
    public void execute(String[] input, ShellState state) throws IOException {
        System.out.println(state.getCurState());
    }
}
