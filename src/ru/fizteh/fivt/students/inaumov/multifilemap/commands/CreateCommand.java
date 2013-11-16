package ru.fizteh.fivt.students.inaumov.multifilemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;

public class CreateCommand<Table, Key, Value, State extends MultiFileMapShellState<Table, Key, Value>> extends AbstractCommand<State> {
    public CreateCommand() {
        super("create", 1);
    }

    public void execute(String[] args, State state) {
        Table newTable = null;

        try {
            newTable = state.createTable(args[1]);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (newTable == null) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }
}
