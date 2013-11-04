package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandCreate implements Command {
    MultiFileShellState state = null;

    public CommandCreate(MultiFileShellState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.createTable(args[1]) == null) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }

    @Override
    public int getArgCount() {
        return 1;
    }
}
