package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;

import java.io.IOException;
import java.util.ArrayList;

public class RmCommand extends AbstractCommand<FileSystemShellState> {
    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() == 0) {
            throw new IOException("missing operand");
        }
        if (parameters.size() > 1) {
            throw new IOException("too many arguments!");
        }
        shellState.getFileSystem().remove(parameters.get(0));
    }

    protected void initCommand() {
        commandName = "rm";
        helpString = "rm <file|directory>";
    }
}
