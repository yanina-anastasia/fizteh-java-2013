package ru.fizteh.fivt.students.inaumov.filemap;

public abstract class AbstractCommand implements Command {
	private final String commandName;
	private final int argumentsNumber;
	
	public AbstractCommand(String commandName, int argumentsNumber) {
		this.commandName = commandName;
		this.argumentsNumber = argumentsNumber;
	}
	
	public String getName() {
		return commandName;
	}
	
	public int getArgumentsNumber() {
		return argumentsNumber;
	}
	
	public abstract void execute(String[] args, ShellState fileMapState)
            throws IllegalArgumentException, UserInterruptionException;
}
