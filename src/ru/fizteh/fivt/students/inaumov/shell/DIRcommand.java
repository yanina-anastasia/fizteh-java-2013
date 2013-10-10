package ru.fizteh.fivt.students.inaumov.shell;

public class DIRcommand extends AbstractCommand {
	public DIRcommand() {
		super("dir", 0);
	}
	
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		if (commandArguments.length - 1 != argumentsNumber) {
			throw new CommandExecutionFailException("dir: expected " + argumentsNumber + " arguments, got " + (commandArguments.length - 1) + " arguments");
		}
		shellState.fileCommander.showCurrentDirectoryContent(System.out);
	}
}
