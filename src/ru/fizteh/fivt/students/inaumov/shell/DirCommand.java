package ru.fizteh.fivt.students.inaumov.shell;

public class DirCommand extends AbstractCommand {
	public DirCommand() {
		super("dir", 0);
	}
	
	public void executeCommand(String[] commandArguments, Shell.ShellState shellState) throws CommandExecutionFailException {
		shellState.fileCommander.showCurrentDirectoryContent();
	}
}
