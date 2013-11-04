package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;
import java.text.ParseException;

public class CommandPut implements Command {

    private StorableShellState state;

    public CommandPut(StorableShellState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "put";
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
            try {
                String key = args[1];
                String valueString = args[2];
                Storeable valueStoreable = state.tableProvider.deserialize(state.currentTable, valueString);
                Storeable oldValue = state.putToCurrentTable(key, valueStoreable);
                if (oldValue == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(oldValue);
                }
            } catch (ParseException e) {
                throw new IOException("put: wrong format", e);
            }
        }
    }
}
