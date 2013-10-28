package ru.fizteh.fivt.students.inaumov.multifilemap;

import ru.fizteh.fivt.students.inaumov.common.AbstractCommand;

public class DropCommand extends AbstractCommand<MultiFileMapShellState> {
    public DropCommand() {
        super("drop", 1);
    }

    public void execute(String[] args, MultiFileMapShellState shellState) {
        try {
            shellState.tableProvider.removeTable(args[1]);
            System.out.println("dropped");
        } catch (IllegalStateException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
