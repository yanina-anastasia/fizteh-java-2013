package ru.fizteh.fivt.students.inaumov.shell;

public class PWDcommand extends AbstractCommand{
	public PWDcommand() {
		super("pwd", 0);
	}
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		if (commandArguments.length - 1 != argumentsNumber) {
			throw new CommandExecutionFailException("pwd: expected " + argumentsNumber + " arguments, got " + (commandArguments.length - 1) + " arguments");
		}
		shellState.fileCommander.showCurrentDirectory(System.out);
	}
}
