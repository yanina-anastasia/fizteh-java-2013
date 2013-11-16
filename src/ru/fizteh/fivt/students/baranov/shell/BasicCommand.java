package ru.fizteh.fivt.students.baranov.shell;

import java.io.IOException;

public interface BasicCommand {
    abstract public void executeCommand(String[] argument, Shell usedShell) throws IOException, ShellInterruptionException;
    abstract public int getNumberOfArguments();
    abstract public String getCommandName();
}
