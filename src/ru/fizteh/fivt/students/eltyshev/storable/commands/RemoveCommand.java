package ru.fizteh.fivt.students.eltyshev.storable.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableShellState;

import java.io.IOException;
import java.util.ArrayList;

public class RemoveCommand extends AbstractCommand<StoreableShellState> {
    public RemoveCommand() {
        super("remove", "remove <key>");
    }

    @Override
    public void executeCommand(String params, StoreableShellState shellState) throws IOException {
        if (shellState.table == null) {
            System.err.println("no table");
            return;
        }

        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 1) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        if (parameters.size() < 1) {
            throw new IllegalArgumentException("argument missing");
        }

        Storeable value = shellState.table.remove(parameters.get(0));
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
