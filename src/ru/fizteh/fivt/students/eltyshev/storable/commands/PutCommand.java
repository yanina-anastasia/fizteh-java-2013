package ru.fizteh.fivt.students.eltyshev.storable.commands;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableShellState;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PutCommand extends AbstractCommand<StoreableShellState> {
    public PutCommand() {
        super("put", "put <key> <xml representation>");
    }

    @Override
    public void executeCommand(String params, StoreableShellState shellState) throws IOException {
        if (shellState.table == null) {
            System.err.println("no table");
            return;
        }

        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 2) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        if (parameters.size() < 2) {
            throw new IllegalArgumentException("argument missing");
        }

        Storeable oldValue = null;
        try {
            Storeable value = shellState.provider.deserialize(shellState.table, parameters.get(1));
            oldValue = shellState.table.put(parameters.get(0), value);
        } catch (ParseException e) {
            System.err.println("incorrect format: " + e.getMessage());
            return;
        }

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}
