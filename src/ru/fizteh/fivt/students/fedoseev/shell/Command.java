package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.IOException;

public interface Command {
    public String getCmdName();

    public Integer getArgsCount();

    public abstract void execute(String[] input, Shell.ShellState state) throws IOException;
}
