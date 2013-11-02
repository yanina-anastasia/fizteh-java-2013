package ru.fizteh.fivt.students.demidov.shell;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public interface BasicCommand {
	abstract public void executeCommand(String[] arguments, Shell usedShell) throws IOException, ShellInterruptionException;
	abstract public int getNumberOfArguments();
	abstract public String getCommandName();
}