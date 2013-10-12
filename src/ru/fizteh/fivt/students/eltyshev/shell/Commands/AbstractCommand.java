package ru.fizteh.fivt.students.eltyshev.shell.commands;

import java.io.IOException;

public abstract class AbstractCommand<State> implements Command<State> {

    public AbstractCommand() {
        initCommand();
    }

    public String getCommandName() {
        return commandName;
    }

    public String getHelpString() {
        return helpString;
    }

    public abstract void executeCommand(String params, State shellState) throws IOException;

    protected abstract void initCommand();

    protected String commandName;
    protected String helpString;
}
