package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.IOException;

public class ExitCommand extends AbstractCommand {
    public ExitCommand(String cmdName, Integer argsCount) {
        super(cmdName, argsCount);
    }

    public void execute(String[] input, Shell.ShellState state) throws IOException {
        if (input.length != getArgsCount()) {
            throw new IOException("EXIT ERROR: \"exit\" command receives no arguments");
        }

        Thread.currentThread().interrupt();
    }
}
