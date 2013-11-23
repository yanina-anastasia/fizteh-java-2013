package ru.fizteh.fivt.students.baranov.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import java.io.IOException;

public class Get extends BasicCommand {
    public String getCommandName() {
        return "get";
    }
    
    public boolean doCommand(String[] arguments, State currentState) throws IOException {
        if (currentState.table == null) {
            System.err.println("Table doesn't exist");
            return false;
        }
        if (arguments.length != 1) {
            System.err.println("Illegal arguments");
            return false;
        }
        Storeable value = currentState.table.get(arguments[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(currentState.table.provider.serialize(currentState.table, value));
        }
        return true;
    }
}
