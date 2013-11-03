package ru.fizteh.fivt.students.anastasyev.shell;

import java.util.Vector;

public abstract class State {
    public abstract Vector<Command> getCommands();
    //public abstract void stopping() throws IOException;
    //public abstract State getMyState(int hashCode) throws IOException;
}
