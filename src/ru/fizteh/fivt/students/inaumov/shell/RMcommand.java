package ru.fizteh.fivt.students.inaumov.shell;

public class RMcommand extends AbstractCommand {
	public RMcommand() {
		super("rm", 1);
	}

	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		if (commandArguments.length - 1 != argumentsNumber) {
			throw new CommandExecutionFailException("rm: expected " + argumentsNumber + " arguments, got " + (commandArguments.length - 1) + " arguments");
		}
		shellState.fileCommander.remove(commandArguments[1]);
	}
}
