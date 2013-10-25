package ru.fizteh.fivt.students.inaumov.filemap;

<<<<<<< HEAD
public abstract class AbstractCommand<State> implements Command<State> {
=======
public abstract class AbstractCommand implements Command {
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
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
	
<<<<<<< HEAD
	public abstract void execute(String[] args, State fileMapState)
=======
	public abstract void execute(String[] args, ShellState fileMapState)
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
            throws IllegalArgumentException, UserInterruptionException;
}
