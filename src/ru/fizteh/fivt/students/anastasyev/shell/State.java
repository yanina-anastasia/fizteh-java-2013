package ru.fizteh.fivt.students.anastasyev.shell;

import java.io.IOException;
import java.util.Vector;

public abstract class State {
    public abstract Vector<Command> getCommands();
    public abstract void save() throws IOException;
    public abstract State getMyState(int hashCode) throws IOException;
}
