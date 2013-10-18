package ru.fizteh.fivt.students.inaumov.shell;

public class CdCommand extends AbstractCommand {
	CdCommand() {
		super("cd", 1);
	}
	
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		String newPath = commandArguments[1];
		shellState.fileCommander.setCurrentDirectory(newPath);
	}
}
