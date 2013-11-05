package ru.fizteh.fivt.students.eltyshev.storable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableShellState;

import java.io.IOException;
import java.util.ArrayList;

public class UseCommand extends AbstractCommand<StoreableShellState> {
    public UseCommand() {
        super("use", "use <table name");
    }

    @Override
    public void executeCommand(String params, StoreableShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments!");
        }
        if (parameters.size() < 1) {
            throw new IllegalArgumentException("argument missing");
        }
        Table newTable = null;
        try {
            newTable = shellState.provider.getTable(parameters.get(0));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (newTable == null) {
            System.err.println(String.format("%s not exists", parameters.get(0)));
            return;
        }

        shellState.table = newTable;
        System.out.println(String.format("using %s", shellState.table.getName()));
    }
}
