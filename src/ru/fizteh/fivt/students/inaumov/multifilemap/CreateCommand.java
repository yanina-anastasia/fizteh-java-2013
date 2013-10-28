package ru.fizteh.fivt.students.inaumov.multifilemap;

import ru.fizteh.fivt.students.inaumov.common.AbstractCommand;

public class CreateCommand extends AbstractCommand<MultiFileMapShellState> {
    public CreateCommand() {
        super("create", 1);
    }

    public void execute(String[] args, MultiFileMapShellState shellState) {
        try {
            shellState.tableProvider.createTable(args[1]);
            System.out.println("created");
        } catch (IllegalStateException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
