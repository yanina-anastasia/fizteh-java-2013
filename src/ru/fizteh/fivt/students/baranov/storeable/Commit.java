package ru.fizteh.fivt.students.baranov.storeable;

import java.io.IOException;

public class Commit extends BasicCommand {
    public String getCommandName() {
        return "commit";
    }
    
    public boolean doCommand(String[] arguments, State currentState) throws IOException {
        if (currentState.table == null) {
            throw new IllegalArgumentException("Wrong table for commit");
        }
        if (arguments.length != 0) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        for (String step: currentState.table.oldData.keySet()) {
            if (currentState.table.get(step) != null) {
                String name = currentState.table.getName();
                currentState.tableProvider.tables.get(name).put(step, currentState.table.get(step));
            }
        }
        currentState.table = currentState.tableProvider.tables.get(currentState.table.getName());
        System.out.println(currentState.table.commit());
        return true;
    }
}

