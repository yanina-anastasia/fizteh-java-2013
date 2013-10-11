package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;
import ru.fizteh.fivt.students.eltyshev.shell.ShellState;

import java.io.IOException;
import java.util.ArrayList;

public class MakeDirCommand extends AbstractCommand {

    public void executeCommand(String params, ShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 1) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        if (parameters.size() < 1) {
            throw new IllegalArgumentException("missing argument");
        }
        FileSystemShellState state = FileSystemShellState.class.cast(shellState);
        state.getFileSystem().createDirectory(parameters.get(0));
    }

    protected void initCommand() {
        commandName = "mkdir";
        helpString = "mkdir <directory name>";
    }
}
