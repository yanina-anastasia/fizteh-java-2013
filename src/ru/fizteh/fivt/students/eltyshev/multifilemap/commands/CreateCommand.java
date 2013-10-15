package ru.fizteh.fivt.students.eltyshev.multifilemap.commands;

import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

import java.util.ArrayList;

public class CreateCommand extends AbstractCommand<MultifileMapShellState> {
    public CreateCommand() {
        super("create", "create <table name>");
    }

    public void executeCommand(String params, MultifileMapShellState shellState) {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments!");
        }
        if (parameters.size() < 1) {
            throw new IllegalArgumentException("argument missing");
        }

        try {
            shellState.tableProvider.createTable(parameters.get(0));
            System.out.println("created");
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
}
