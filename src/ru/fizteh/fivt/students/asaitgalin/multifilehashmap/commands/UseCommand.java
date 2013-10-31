package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableState;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.extensions.ChangesCountingTable;
import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class UseCommand implements Command {
    private MultiFileTableState state;

    public UseCommand(MultiFileTableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public void execute(String[] args) throws IOException {
        int changes = state.currentTable.getChangesCount();
        if (changes != 0) {
            System.out.println(changes + " unsaved changes");
        } else {
            ChangesCountingTable table = state.provider.getTable(args[1]);
            if (table != null) {
                state.currentTable = table;
                System.out.println("using " + args[1]);
            } else {
                System.out.println(args[1] + " not exists");
            }
        }
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
