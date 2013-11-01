package ru.fizteh.fivt.students.asaitgalin.multifilehashmap.commands;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.MultiFileTableState;
import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class CommitCommand implements Command {
    private MultiFileTableState state;

    public CommitCommand(MultiFileTableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "commit";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.currentTable == null) {
            System.out.println("no table");
        } else {
            System.out.println(state.currentTable.commit());
        }
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
