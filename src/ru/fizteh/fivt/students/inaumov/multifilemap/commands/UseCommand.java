package ru.fizteh.fivt.students.inaumov.multifilemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;
import ru.fizteh.fivt.storage.strings.Table;

public class UseCommand<Table, Key, Value, State extends MultiFileMapShellState<Table, Key, Value>> extends AbstractCommand<State> {
    public UseCommand() {
        super("use", 1);
    }

    public void execute(String[] args, State state) {
        Table newTable = null;

        try {
            newTable = state.useTable(args[1]);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (newTable == null) {
            System.out.println(args[1] + " not exists");
            return;
        }

        System.out.println("using " + state.getCurrentTableName());
    }
}
