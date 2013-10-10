package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.IOException;

public interface Shell {
    public String join(String[] items, String sep);

    public String[] getCommandArguments(String inputString);

    public abstract void run() throws IOException, InterruptedException;
}
