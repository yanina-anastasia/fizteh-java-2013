//package ru.fizteh.fivt.students.inaumov.shell;

public class ExitCommand extends AbstractCommand {
	public ExitCommand() {
		super("exit", 0);
	}
	
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException, UserInterruptionException {
		throw new UserInterruptionException();
	}
}
