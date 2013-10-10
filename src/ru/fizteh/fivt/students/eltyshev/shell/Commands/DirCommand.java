package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;
import ru.fizteh.fivt.students.eltyshev.shell.ShellState;

import java.io.IOException;

public class DirCommand extends AbstractCommand {

    public void executeCommand(String params, ShellState shellState) throws IOException {
        if (params.length() > 0) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        FileSystemShellState state = FileSystemShellState.class.cast(shellState);
        String[] files = state.getFileSystem().listWorkingDirectory();
        if (files == null) {
            return;
        }
        for (final String file : files) {
            System.out.println(file);
        }
    }

    protected void initCommand() {
        commandName = "dir";
        helpString = "dir";
    }
}
