package ru.fizteh.fivt.students.baranov.storeable;

import java.io.IOException;

public class Drop extends BasicCommand {
    public String getCommandName() {
       return "drop";
    }
    
    public boolean doCommand(String[] arguments, State currentState) throws IOException {
        if (arguments.length != 1) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        String path = currentState.getProperty(currentState);
        if (!currentState.tableProvider.tables.containsKey(arguments[0])) {
            System.out.println(arguments[0] + " not exists");
            return false;
        }
        if (currentState.table != null && arguments[0].equals(currentState.table.getName())) {
            currentState.table = null;
        }
        currentState.tableProvider.removeTable(arguments[0]);

        System.out.println("dropped");
        return true;
    }
}