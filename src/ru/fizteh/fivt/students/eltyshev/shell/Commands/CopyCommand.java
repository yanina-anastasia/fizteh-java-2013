package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;
import ru.fizteh.fivt.students.eltyshev.shell.ShellState;

import java.util.ArrayList;
import java.io.IOException;

public class CopyCommand extends AbstractCommand {
    public void executeCommand(String params, ShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 2) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        FileSystemShellState state = FileSystemShellState.class.cast(shellState);
        state.getFileSystem().copyFiles(parameters.get(0), parameters.get(1));
    }

    public void initCommand() {
        commandName = "cp";
        helpString = "cp <source> <destination>";
    }
}
