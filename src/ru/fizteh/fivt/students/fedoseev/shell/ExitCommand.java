package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.IOException;

public class ExitCommand extends AbstractCommand {
    public ExitCommand(String cmdName, Integer argsCount) {
        super(cmdName, argsCount);
    }

    public void execute(String[] input, AbstractShell.ShellState state) throws IOException {
        Thread.currentThread().interrupt();
    }
}
