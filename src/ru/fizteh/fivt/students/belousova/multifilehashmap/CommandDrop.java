package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandDrop implements Command {
    MultiFileShellState state = null;

    public CommandDrop(MultiFileShellState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.getTable(args[1]) == null) {
            System.out.println(args[1] + " not exists");
        } else {
            if (state.getCurrentTable()!= null && state.getCurrentTable().equals(args[1])) {
                state.resetCurrentTable();
            }
            System.out.println("dropped");
            state.removeTable(args[1]);
        }
    }

    @Override
    public int getArgCount() {
        return 1;
    }
}
