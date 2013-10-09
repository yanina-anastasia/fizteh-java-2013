package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.IOException;

public class PwdCommand extends Command {
    public PwdCommand(String cmdName, Integer argsCount) {
        super(cmdName, argsCount);
    }

    public void execute(String[] input, Shell.ShellState state) throws IOException {
        if (input.length != getArgsCount()) {
            throw new IOException("PWD ERROR: \"pwd\" command receives no arguments");
        }

        System.out.println(state.getCurState());
    }
}
