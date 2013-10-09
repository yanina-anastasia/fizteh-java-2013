package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystem;
import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;
import ru.fizteh.fivt.students.eltyshev.shell.ShellState;

import java.io.IOException;
import java.util.ArrayList;

public class PwdCommand extends Command {
    public void executeCommand(String params, ShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 0) {
            throw new IOException("too many arguments");
        }
        FileSystemShellState state = FileSystemShellState.class.cast(shellState);
        String path = state.getFileSystem().getWorkingDirectory();
        System.out.println(path);
    }

    protected void initCommand() {
        commandName = "pwd";
        helpString = "pwd";
    }
}
