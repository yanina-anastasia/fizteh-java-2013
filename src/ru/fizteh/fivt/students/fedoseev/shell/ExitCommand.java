package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.IOException;

public class ExitCommand extends AbstractCommand {
    public ExitCommand() {
        super("exit", 0);
    }

    public void execute(String[] input, AbstractShell.ShellState state) throws IOException {
        Thread.currentThread().interrupt();
    }
}
