package ru.fizteh.fivt.students.baranov.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import java.io.IOException;
import java.text.ParseException;

public class Put extends BasicCommand {
    public String getCommandName() {
        return "put";
    }
    
    public boolean doCommand(String[] arguments, State currentState) {
        if (currentState.table == null) {
            System.err.println("Table doesn't exist");
            return false;
        }
        if (arguments.length != 2) {
            System.err.println("Illegal arguments");
            return false;
        }
        Storeable value;
        try {
            value = currentState.table.provider.deserialize(currentState.table, arguments[1]);
        } catch (ParseException e) {
            System.err.println("ParseException" + e.getMessage());
            return false;
        }
        Storeable prevValue = currentState.table.put(arguments[0], value);
        if (prevValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(currentState.table.provider.serialize(currentState.table, prevValue));
        }
        return true;
    }    
}
