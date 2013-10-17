package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.IOException;

public interface CommandAbstract {
    public void exit() throws IOException;
    public String startShellString() throws IOException;
    public boolean selfParsing();
}
