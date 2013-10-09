package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.IOException;

public class CopyFileOrDirectory extends Command {

	public CopyFileOrDirectory() {
		super("cp", 2);
	}
	
	@Override
	public void execute(String[] argumentsList) {
		super.getArgsAcceptor(argumentsList.length - 1);
		if(super.hasError) {
			return;
		}
		
		File filePath = new File(Shell.currentDirectory + File.separator + argumentsList[1]);
		File destination = new File(argumentsList[2]);
		
		if(!destination.isAbsolute()) {
			destination = new File(Shell.currentDirectory+ File.separator + destination);
		}
		if(!destination.exists() || !destination.isDirectory()) {
			Shell.generateAnError("\"" + destination + "\": No such directory", this.getName() );
			return;
		}
		if(!filePath.exists()) {
			Shell.generateAnError("\"" + argumentsList[1] + "\": No such file or directory", this.getName() );
			return;
		}
		File newFile = new File(destination + File.separator + argumentsList[1]);
		try {
			if(newFile.exists()) {
				Shell.generateAnError("File or directory with name \"" + argumentsList[1] 
						+ "\" already exists in directory with path \""+ destination.getCanonicalPath() + "\"", this.getName() );
				return;
			}
			super.copying(filePath, destination);
			if(super.hasError) {
				return;
			}
		} catch (IOException e) {}
	}
}
