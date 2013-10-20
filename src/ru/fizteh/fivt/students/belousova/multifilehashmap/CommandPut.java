package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandPut implements Command {
    private static final String name = "put";
    private MultiFileShellState state;

    public CommandPut(MultiFileShellState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getArgCount() {
        return 2;
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
        } else {
            String key = args[1];
            String value = args[2];
            String oldValue = state.putToCurrentTable(key, value);
            if (oldValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(oldValue);
            }
        }
    }
}
