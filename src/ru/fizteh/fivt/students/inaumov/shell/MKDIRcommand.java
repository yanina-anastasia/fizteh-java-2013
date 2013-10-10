//package ru.fizteh.fivt.students.inaumov.shell;

public class MkdirCommand extends AbstractCommand {
	public MkdirCommand() {
		super("mkdir", 1);
	}
	
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException{
		shellState.fileCommander.createNewDirectory(commandArguments[1]);
	}
}
