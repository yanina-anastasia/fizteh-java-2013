package ru.fizteh.fivt.students.eltyshev.storable.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableShellState;

import java.io.IOException;

public class ExitCommand extends AbstractCommand<StoreableShellState> {

    public ExitCommand() {
        super("exit", "exit");
    }

    @Override
    public void executeCommand(String params, StoreableShellState shellState) throws IOException {
        if (CommandParser.getParametersCount(params) > 0) {
            throw new IllegalArgumentException("too many parameters");
        }
        if (shellState.table != null) {
            shellState.table.commit();
        }
        System.exit(0);
    }
}
