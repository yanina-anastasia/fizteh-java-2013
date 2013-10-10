package ru.fizteh.fivt.students.inaumov.shell;

public class EXITcommand extends AbstractCommand {
	public EXITcommand() {
		super("exit", 0);
	}
	
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException, UserInterruptionException {
		if (commandArguments.length - 1 != argumentsNumber) {
			throw new CommandExecutionFailException("exit: expected " + argumentsNumber + " arguments, got " + (commandArguments.length - 1) + " arguments");
		}
		throw new UserInterruptionException();
	}
}
