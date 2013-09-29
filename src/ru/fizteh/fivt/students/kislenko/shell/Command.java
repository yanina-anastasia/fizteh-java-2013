package ru.fizteh.fivt.students.kislenko.shell;

import java.io.IOException;

public interface Command {
    public void run(String arg) throws IOException;
}