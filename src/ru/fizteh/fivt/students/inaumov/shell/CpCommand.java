package ru.fizteh.fivt.students.inaumov.shell;

import java.io.IOException;

public class CpCommand extends AbstractCommand {
	public CpCommand() {
		super("cp", 2);
	}
	
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		try {
			shellState.fileCommander.copyFiles(commandArguments[1], commandArguments[2]);
		} catch (IOException exception) {
			throw new CommandExecutionFailException("cp: " + exception.getMessage());
		}
	}
}
