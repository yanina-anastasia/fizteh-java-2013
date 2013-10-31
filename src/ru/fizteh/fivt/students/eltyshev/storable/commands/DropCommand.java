package ru.fizteh.fivt.students.eltyshev.storable.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableShellState;

import java.io.IOException;
import java.util.ArrayList;

public class DropCommand extends AbstractCommand<StoreableShellState> {

    public DropCommand() {
        super("drop", "drop <table name>");
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

        try {
            shellState.provider.removeTable(parameters.get(0));
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}
