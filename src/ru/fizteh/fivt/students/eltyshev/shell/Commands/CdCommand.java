package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystem;
import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;
import ru.fizteh.fivt.students.eltyshev.shell.ShellState;

import java.io.IOException;
import java.util.ArrayList;

public class CdCommand extends Command {
    public void executeCommand(String params, ShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 1) {
            throw new IllegalArgumentException("Too many arguments");
        }
        FileSystemShellState state = FileSystemShellState.class.cast(shellState);
        if (parameters.size() > 0) {
            state.getFileSystem().setWorkingDirectory(parameters.get(0));
        }
    }

    protected void initCommand() {
        commandName = "cd";
        helpString = "cd <directory name>";
    }
}
