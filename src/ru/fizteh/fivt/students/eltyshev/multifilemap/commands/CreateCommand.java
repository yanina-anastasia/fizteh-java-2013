package ru.fizteh.fivt.students.eltyshev.multifilemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
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

        Table newTable = shellState.tableProvider.createTable(parameters.get(0));
        if (newTable == null) {
            System.out.println(String.format("%s exists", parameters.get(0)));
        } else {
            System.out.println("created");
        }
    }
}
