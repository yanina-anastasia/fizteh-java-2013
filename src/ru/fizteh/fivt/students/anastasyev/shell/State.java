package ru.fizteh.fivt.students.anastasyev.shell;

import java.util.Vector;

public abstract class State {
    private Vector<Command> allCommands;

    public Vector<Command> getCommands() {
        return allCommands;
    }
}
