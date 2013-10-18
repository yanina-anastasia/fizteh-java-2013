package ru.fizteh.fivt.students.dmitryIvanovsky.shell;

import java.io.IOException;

public interface CommandAbstract {
    void exit() throws IOException;
    String startShellString() throws IOException;
    boolean selfParsing();
}
