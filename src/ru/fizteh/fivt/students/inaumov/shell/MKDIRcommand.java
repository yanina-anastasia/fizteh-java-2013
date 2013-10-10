package ru.fizteh.fivt.students.inaumov.shell;

public class MKDIRcommand extends AbstractCommand {
	public MKDIRcommand() {
		super("mkdir", 1);
	}
	
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException{
		if (commandArguments.length - 1 != argumentsNumber) {
			throw new CommandExecutionFailException("mkdir: expected " + argumentsNumber + " arguments, got " + (commandArguments.length - 1) + " arguments");
		}
		shellState.fileCommander.createNewDirectory(commandArguments[1]);
	}
}
