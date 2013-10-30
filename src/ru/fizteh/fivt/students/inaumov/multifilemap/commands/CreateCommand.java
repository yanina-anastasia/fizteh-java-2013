package ru.fizteh.fivt.students.inaumov.multifilemap.commands;

import ru.fizteh.fivt.students.inaumov.shell.base.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapShellState;

public class CreateCommand extends AbstractCommand<MultiFileMapShellState> {
    public CreateCommand() {
        super("create", 1);
    }

    public void execute(String[] args, MultiFileMapShellState shellState) {
        try {
            shellState.tableProvider.createTable(args[1]);
            System.out.println("created");
        } catch (IllegalStateException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
