package ru.fizteh.fivt.students.baranov.storeable;

import java.io.IOException;

public abstract class BasicCommand {
    public BasicCommand() {
    }

    public abstract boolean doCommand(String[] arguments, State state) throws IOException;

    public abstract String getCommandName();
}