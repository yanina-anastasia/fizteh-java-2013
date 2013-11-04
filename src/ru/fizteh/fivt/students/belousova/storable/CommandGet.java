package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandGet implements Command {
    private static final String name = "get";
    private StorableShellState state;

    public CommandGet(StorableShellState state) {
        this.state = state;
    }

    @Override
    public int getArgCount() {
        return 1;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
        } else {
            String key = args[1];
            Storeable value = state.getFromCurrentTable(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(state.tableProvider.serialize(state.currentTable, value));
            }
        }
    }

}
