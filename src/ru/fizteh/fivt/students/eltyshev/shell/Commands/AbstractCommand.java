package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.ShellState;

import java.io.IOException;

public abstract class AbstractCommand implements Command {

    public AbstractCommand() {
        initCommand();
    }

    public String getCommandName() {
        return commandName;
    }

    public String getHelpString() {
        return helpString;
    }

    public abstract void executeCommand(String params, ShellState shellState) throws IOException;

    protected abstract void initCommand();

    protected String commandName;
    protected String helpString;
}
