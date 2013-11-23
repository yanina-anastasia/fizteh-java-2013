package ru.fizteh.fivt.students.baranov.storeable;

import java.io.IOException;

public class Size extends BasicCommand {
    public String getCommandName() {
        return "size";
    }
    
    public boolean doCommand(String[] arguments, State currentState) throws IOException {
        if (currentState.table == null) {
            throw new IllegalArgumentException("no table");
        }
        if (arguments.length != 0) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        System.out.println(currentState.table.size());
        return true;
    }
}
