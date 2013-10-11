package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.IOException;

public interface Command {
    public String getName();

    public void executeCmd(Shell shell, String[] args) throws IOException;
}
