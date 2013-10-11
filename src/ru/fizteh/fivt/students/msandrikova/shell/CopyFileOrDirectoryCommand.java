package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.IOException;

public class CopyFileOrDirectoryCommand extends Command {

	public CopyFileOrDirectoryCommand() {
		super("cp", 2);
	}
	
	@Override
	public File execute(String[] argumentsList, boolean isInteractive, File currentDirectory) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, isInteractive)) {
			return currentDirectory;
		}
		
		File filePath = new File(currentDirectory + File.separator + argumentsList[1]).getAbsoluteFile();
		File destination = new File(argumentsList[2]).getAbsoluteFile();
		
		if(destination.equals(filePath)) {
			Utils.generateAnError("Source and destination files should be different.", this.getName(), isInteractive );
			return currentDirectory;
		}
		
		if(!destination.isAbsolute()) {
			destination = new File(currentDirectory+ File.separator + destination);
		}
		if(!destination.exists()) {
			Utils.generateAnError("\"" + destination + "\": No such directory or file.", this.getName(), isInteractive );
			return currentDirectory;
		}
		if(!filePath.exists()) {
			Utils.generateAnError("\"" + argumentsList[1] + "\": No such file or directory.", this.getName(), isInteractive );
			return currentDirectory;
		}
		if(!destination.isDirectory()) {
			try {
				Utils.copyFiles(filePath, destination);
				return currentDirectory;
			} catch (IOException e) {}
		}
		File newFile = new File(destination + File.separator + argumentsList[1]);
		try {
			if(newFile.exists()) {
				Utils.generateAnError("File or directory with name \"" + argumentsList[1] 
						+ "\" already exists in directory with path \""+ destination.getCanonicalPath() + "\"", this.getName(), isInteractive );
				return currentDirectory;
			}
			if(!Utils.copying(filePath, destination, this.getName(), isInteractive)) {
				return currentDirectory;
			}
		} catch (IOException e) {}
		return currentDirectory;
	}
}
