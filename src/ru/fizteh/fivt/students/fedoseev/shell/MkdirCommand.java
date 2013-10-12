package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.File;
import java.io.IOException;

public class MkdirCommand extends AbstractCommand {
    public MkdirCommand(String cmdName, Integer argsCount) {
        super(cmdName, argsCount);
    }

    public void execute(String[] input, AbstractShell.ShellState state) throws IOException {
        File newDir = new File(state.getCurState().toPath().resolve(input[0]).toString());

        if (!newDir.mkdirs()) {
            throw new IOException("MKDIR ERROR: directory \"" + input[0] +
                    "\" already exists or some other error has happened");
        }
    }
}
