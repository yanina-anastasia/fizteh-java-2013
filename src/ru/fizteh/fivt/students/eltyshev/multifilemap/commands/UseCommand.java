package ru.fizteh.fivt.students.eltyshev.multifilemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.eltyshev.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.eltyshev.shell.commands.CommandParser;

import java.util.ArrayList;

public class UseCommand extends AbstractCommand<MultifileMapShellState> {
    public UseCommand() {
        super("use", "use <table name>");
    }

    public void executeCommand(String params, MultifileMapShellState shellState) {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments!");
        }
        if (parameters.size() < 1) {
            throw new IllegalArgumentException("argument missing");
        }
        Table oldTable = shellState.table;
        try {
            shellState.table = shellState.tableProvider.getTable(parameters.get(0));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
        if (oldTable != null) {
            oldTable.commit();
        }
    }
}
