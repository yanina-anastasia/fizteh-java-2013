package ru.fizteh.fivt.students.baranov.storeable;

import java.io.IOException;

public class Exit extends BasicCommand {
    public String getCommandName() {
        return "exit";
    }
    
    public boolean doCommand(String[] arguments, State currentState) throws IOException {
        if (arguments.length != 0) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        return true;
    }
}
