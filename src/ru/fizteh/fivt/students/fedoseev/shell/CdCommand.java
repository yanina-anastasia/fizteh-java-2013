package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class CdCommand extends AbstractCommand {
    public CdCommand(String cmdName, Integer argsCount) {
        super(cmdName, argsCount);
    }

    public void execute(String[] input, AbstractShell.ShellState state) throws IOException {
        if (input.length != getArgsCount()) {
            throw new IOException("CD ERROR: \"cd\" command receives only 1 argument");
        }

        Path toCanPath = state.getCurState().toPath().resolve(input[0]).normalize();
        File newDir = new File(toCanPath.toString());

        if (!newDir.isDirectory()) {
            throw new FileNotFoundException("CD ERROR: not existing directory \"" + input[0] +
                    "\" in current directory");
        }

        state.setCurState(toCanPath.toFile());
    }
}
