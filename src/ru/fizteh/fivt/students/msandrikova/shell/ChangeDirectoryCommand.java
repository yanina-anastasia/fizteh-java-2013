package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;

public class ChangeDirectoryCommand extends Command {

	public ChangeDirectoryCommand() {
		super("cd", 1);
	}
	
	@Override
	public File execute(String[] argumentsList, boolean isInteractive, File currentDirectory) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, isInteractive)) {
			return currentDirectory;
		}
		
		File filePath = new File(argumentsList[1]);
		
		if(!filePath.isAbsolute()) {
			filePath = new File(currentDirectory+ File.separator + argumentsList[1]);
		}
		if(!filePath.exists() || !filePath.isDirectory()) {
			Utils.generateAnError("\"" + argumentsList[1] + "\": No such directory.", this.getName(), isInteractive);
			return currentDirectory;
		}
		currentDirectory = filePath;
		return currentDirectory;
	}
}
