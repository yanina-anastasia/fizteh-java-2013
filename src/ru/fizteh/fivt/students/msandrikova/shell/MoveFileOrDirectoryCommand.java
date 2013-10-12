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

		File destination = new File(argumentsList[2]);
		if(!destination.isAbsolute()) {
			destination = new File(currentDirectory+ File.separator + destination);
		}
		destination = destination.getAbsoluteFile();
		File filePath = new File(argumentsList[1]);
		if(!filePath.isAbsolute()) {
			filePath = new File(currentDirectory+ File.separator + filePath);
		}
		filePath = filePath.getAbsoluteFile();
		
		if(filePath.equals(destination)) {
			Utils.generateAnError("Source and destination should be different", this.getName(), isInteractive );
			return currentDirectory;
		}
		if(!filePath.exists()) {
			Utils.generateAnError("\"" + argumentsList[1] + "\": No such file or directory", this.getName(), isInteractive );
			return currentDirectory;
		}
		
		if(destination.exists()) {
			if(destination.isDirectory()) {
				try {
					if(!Utils.copying(filePath, destination, this.getName(), isInteractive)) {
						return currentDirectory;
					}
					if(!Utils.remover(filePath, this.getName(), isInteractive)) {
						return currentDirectory;
					}
				} catch (IOException e) {}
			} else {
				Utils.generateAnError("Can not move in existing file: \"" + argumentsList[2] + "\"", this.getName(), isInteractive );
				return currentDirectory;
			}
			
		} else {
			try {
				if(destination.getCanonicalFile().getParentFile().equals(filePath.getCanonicalFile().getParentFile())) {
					boolean destinationIsDirectory = argumentsList[2].endsWith(File.separator);
					if(destinationIsDirectory == filePath.isDirectory()) {
						filePath.renameTo(destination);
					} else {
						Utils.generateAnError("Can not rename file and get directory or rename directory and get file.", this.getName(), isInteractive );
						return currentDirectory;
					}
				} else {
					Utils.generateAnError("Destination does not exist and does not locate in the same directory with source.", this.getName(), isInteractive );
					return currentDirectory;
				}
			} catch (IOException e) {}
		}
		return currentDirectory;
	}

}
