package ru.fizteh.fivt.students.inaumov.shell;

public class RmCommand extends AbstractCommand {
	public RmCommand() {
		super("rm", 1);
	}

	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		shellState.fileCommander.remove(commandArguments[1]);
	}
}
