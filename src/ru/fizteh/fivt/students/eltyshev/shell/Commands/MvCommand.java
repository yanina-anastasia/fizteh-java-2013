package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystem;
import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;
import ru.fizteh.fivt.students.eltyshev.shell.ShellState;

import java.io.IOException;
import java.util.ArrayList;

public class MvCommand extends Command {
    public void executeCommand(String params, ShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 2) {
            throw new IOException("too many arguments!");
        }
        if (parameters.size() < 2) {
            throw new IOException("missing operand");
        }
        FileSystemShellState state = FileSystemShellState.class.cast(shellState);
        state.getFileSystem().moveFiles(parameters.get(0), parameters.get(1));
    }

    protected void initCommand() {
        commandName = "mv";
        helpString = "mv <source> <destination>";
    }
}
