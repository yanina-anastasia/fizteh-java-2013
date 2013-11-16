package ru.fizteh.fivt.students.inaumov.storeable.commands;

import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;
import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.storeable.StoreableShellState;
import ru.fizteh.fivt.storage.structured.Table;

public class StoreableCreateCommand<Table, Key, Value, State extends MultiFileMapShellState<Table, Key, Value>>
        extends AbstractCommand<State> {
    public StoreableCreateCommand() {
        super("create", -1);
    }

    public void execute(String[] args, State state) {
        if (args.length <= 2) {
            throw new IllegalArgumentException("error: expected table name and value types");
        }

        String tableName = args[1];
        Table table = null;

        StringBuilder stringBuilder = new StringBuilder();
        boolean firstEntry = true;

        for (int i = 1 ; i < args.length; ++i) {
            if (firstEntry) {
                firstEntry = false;
            } else {
                stringBuilder.append(' ');
            }
            stringBuilder.append(args[i].trim());
        }

        try {
            table = state.createTable(stringBuilder.toString());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (table == null) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }
}
