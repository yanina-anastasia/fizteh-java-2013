package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.ShellState;

import java.io.IOException;

public interface Command {
    public String getCommandName();

    public String getHelpString();

    public void executeCommand(String params, ShellState shellState) throws IOException;
}
