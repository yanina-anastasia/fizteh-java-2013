package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;

public class ExitCommand extends Command {

	public ExitCommand() {
		super("exit", 0);
	}
	
	@Override
	public File execute(String[] argumentsList, boolean isInteractive, File currentDirectory) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, isInteractive)) {
			return currentDirectory;
		}
		
		Thread.currentThread().interrupt();
		return currentDirectory;
	}
}
