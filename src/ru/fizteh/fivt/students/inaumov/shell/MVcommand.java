package ru.fizteh.fivt.students.inaumov.shell;

public class MVcommand extends AbstractCommand {
	public MVcommand() {
		super("mv", 2);
	}
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		if (commandArguments.length - 1 != argumentsNumber) {
			throw new CommandExecutionFailException("mv: expected " + argumentsNumber + " arguments, got " + (commandArguments.length - 1) + " arguments");
		}
		shellState.fileCommander.moveFiles(commandArguments[1], commandArguments[2]);
	}
}
