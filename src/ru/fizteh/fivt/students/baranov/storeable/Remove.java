package ru.fizteh.fivt.students.baranov.storeable;

import java.io.IOException;

public class Remove extends BasicCommand {
    public String getCommandName() {
        return "remove";
    }
    
    public boolean doCommand(String[] arguments, State currentState) throws IOException {
        if (currentState.table == null) {
            throw new IllegalArgumentException("no table");
        }
        if (arguments.length != 1) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        
        if (currentState.table.remove(arguments[0]) != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        
        return true;
    }
}
