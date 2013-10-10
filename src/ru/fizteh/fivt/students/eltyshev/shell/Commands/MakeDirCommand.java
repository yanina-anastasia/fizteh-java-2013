package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;
import ru.fizteh.fivt.students.eltyshev.shell.ShellState;

import java.io.IOException;

public class MakeDirCommand extends AbstractCommand {

    public void executeCommand(String params, ShellState shellState) throws IOException {

        if (CommandParser.getParametersCount(params) > 1) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        FileSystemShellState state = FileSystemShellState.class.cast(shellState);
        state.getFileSystem().createDirectory(params);
    }

    protected void initCommand() {
        commandName = "mkdir";
        helpString = "mkdir <directory name>";
    }
}
