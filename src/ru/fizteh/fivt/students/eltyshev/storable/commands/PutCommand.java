package ru.fizteh.fivt.students.eltyshev.storable.commands;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableShellState;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PutCommand extends AbstractCommand<StoreableShellState> {
    public PutCommand() {
        super("put", "put <key> <value1> [<value2> ...]");
    }

    @Override
    public void executeCommand(String params, StoreableShellState shellState) throws IOException {
        if (shellState.table == null) {
            System.err.println("no table");
            return;
        }

        int argumentsCount = shellState.table.getColumnsCount() + 1;

        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > argumentsCount) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        if (parameters.size() < argumentsCount) {
            throw new IllegalArgumentException("argument missing");
        }

        List<Object> values = null;
        try {
            values = StoreableUtils.parseValues(parameters, shellState.table);
        } catch (ColumnFormatException e) {
            System.err.println("incorrect value");
            return;
        }

        Storeable value = shellState.provider.createFor(shellState.table, values);
        Storeable oldValue = shellState.table.put(parameters.get(0), value);

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}
