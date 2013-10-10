package ru.fizteh.fivt.students.inaumov.shell;

public interface Command {
	public String getName();
	abstract void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws UserInterruptionException, CommandExecutionFailException;
}
