package ru.fizteh.fivt.students.inaumov.shell;

public class PwdCommand extends AbstractCommand{
	public PwdCommand() {
		super("pwd", 0);
	}
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		shellState.fileCommander.showCurrentDirectory();
	}
}
