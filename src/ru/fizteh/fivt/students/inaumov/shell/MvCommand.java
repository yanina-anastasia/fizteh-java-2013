package ru.fizteh.fivt.students.inaumov.shell;

public class MvCommand extends AbstractCommand {
	public MvCommand() {
		super("mv", 2);
	}
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		shellState.fileCommander.moveFiles(commandArguments[1], commandArguments[2]);
	}
}
