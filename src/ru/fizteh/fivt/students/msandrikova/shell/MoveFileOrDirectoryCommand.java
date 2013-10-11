package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.IOException;

public class MoveFileOrDirectoryCommand extends Command {

	public MoveFileOrDirectoryCommand() {
		super("mv", 2);
	}
	
	@Override
	public File execute(String[] argumentsList, boolean isInteractive, File currentDirectory) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, isInteractive)) {
			return currentDirectory;
		}

		File filePath = new File(currentDirectory + File.separator + argumentsList[1]);
		File destination = new File(argumentsList[2]);
		if(!destination.isAbsolute()) {
			destination = new File(currentDirectory+ File.separator + destination);
		}
		if(!destination.exists()) {
			Utils.generateAnError("\"" + argumentsList[2] + "\": No such file or directory", this.getName(), isInteractive );
			return currentDirectory;
		}
		if(!filePath.exists()) {
			Utils.generateAnError("\"" + argumentsList[1] + "\": No such file or directory", this.getName(), isInteractive );
			return currentDirectory;
		}
		try {
			if(destination.getCanonicalFile().getParent().equals(filePath.getCanonicalFile().getParent())) {
				if(destination.getAbsoluteFile().equals(filePath.getAbsoluteFile())) {
					return currentDirectory;
				}
				if(!Utils.remover(destination, this.getName(), isInteractive)) {
					return currentDirectory;
				}
				filePath.renameTo(destination);
			} else {
				if(!Utils.copying(filePath, destination, this.getName(), isInteractive)) {
					return currentDirectory;
				}
				
				if(!Utils.remover(filePath, this.getName(), isInteractive)) {
					return currentDirectory;
				}
			}
		} catch (IOException e) {}	
		return currentDirectory;
	}

}
