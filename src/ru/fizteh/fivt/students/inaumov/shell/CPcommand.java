package ru.fizteh.fivt.students.inaumov.shell;

import java.io.IOException;

public class CPcommand extends AbstractCommand {
	public CPcommand() {
		super("cp", 2);
	}
	
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		if (commandArguments.length - 1 != argumentsNumber) {
			throw new CommandExecutionFailException("cp: expected " + argumentsNumber + " arguments, got " + (commandArguments.length - 1) + " arguments");
		}
		try {
			shellState.fileCommander.copyFiles(commandArguments[1], commandArguments[2]);
		} catch (IOException exception) {
			throw new CommandExecutionFailException("cp: " + exception.getMessage());
		}
	}
}
