package ru.fizteh.fivt.students.inaumov.shell;

public class AbstractCommand implements Command {
	protected String commandName;
	protected int argumentsNumber;
	
	public AbstractCommand(String commandName, int argumentsNumber) {
		this.commandName = commandName;
		this.argumentsNumber = argumentsNumber;
	}
	public String getName() {
		return commandName;
	}
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws UserInterruptionException, CommandExecutionFailException { };
}