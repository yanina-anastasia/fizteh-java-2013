package ru.fizteh.fivt.students.eltyshev.storable.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableShellState;

import java.io.IOException;

public class SizeCommand extends AbstractCommand<StoreableShellState> {
    public SizeCommand() {
        super("size", "size");
    }

    @Override
    public void executeCommand(String params, StoreableShellState shellState) throws IOException {
        if (CommandParser.getParametersCount(params) > 0) {
            throw new IllegalArgumentException("too many parameters");
        }
        if (shellState.table == null) {
            System.err.println("no table");
            return;
        }
        int size = shellState.table.size();
        System.out.println(size);
    }
}
