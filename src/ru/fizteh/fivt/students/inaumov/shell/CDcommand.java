package ru.fizteh.fivt.students.inaumov.shell;

public class CDcommand extends AbstractCommand {
	CDcommand() {
		super("cd", 1);
	}
	
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		if (commandArguments.length - 1 != argumentsNumber) {
			throw new CommandExecutionFailException("cd: expected " + argumentsNumber + " arguments, got " + (commandArguments.length - 1) + " arguments");
		}
		String newPath = commandArguments[1];
		shellState.fileCommander.setCurrentDirectory(newPath);
	}
}
