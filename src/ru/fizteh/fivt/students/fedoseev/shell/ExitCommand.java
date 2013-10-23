package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.IOException;

public class ExitCommand extends AbstractCommand<ShellState> {
    public ExitCommand() {
        super("exit", 0);
    }

    @Override
    public void execute(String[] input, ShellState state) throws IOException {
        Thread.currentThread().interrupt();
    }
}
