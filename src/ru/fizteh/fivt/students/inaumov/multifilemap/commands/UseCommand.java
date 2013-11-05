package ru.fizteh.fivt.students.inaumov.multifilemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;
import ru.fizteh.fivt.storage.strings.Table;

public class UseCommand extends AbstractCommand<MultiFileMapShellState> {
    public UseCommand() {
        super("use", 1);
    }

    public void execute(String[] args, MultiFileMapShellState shellState) {
        Table oldTable = shellState.table;

        try {
            shellState.table = shellState.tableProvider.getTable(args[1]);
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            return;
        } catch (IllegalStateException exception) {
            System.err.println(exception.getMessage());
            return;
        }

        if (oldTable != null) {
            oldTable.commit();
        }

        System.out.println("using " + shellState.table.getName());
    }
}
