package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.belousova.shell.Command;

public class CommandExit implements Command {
    private final String name = "exit";
    private MultiFileShellState state;

    public CommandExit(MultiFileShellState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @Override
    public void execute(String[] args) {
        if (state.getCurrentTable() != null) {
            state.commitCurrentTable();
        }
        System.out.println("exit");
        System.exit(0);
    }
}