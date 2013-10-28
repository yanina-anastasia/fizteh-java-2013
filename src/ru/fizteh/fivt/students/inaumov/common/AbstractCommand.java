package ru.fizteh.fivt.students.inaumov.common;

public abstract class AbstractCommand<State> implements Command<State> {
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

	public abstract void execute(String[] args, State fileMapState) throws UserInterruptionException;
}
