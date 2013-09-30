package ru.fizteh.fivt.students.belousova.shell;

import java.io.IOException;

public interface Command {
    public String getName();

    public void execute(String args) throws IOException;
}
