package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;

public class ChangeDirectory extends Command {

	public ChangeDirectory() {
		super("cd", 1);
	}
	
	@Override
	public void execute(String[] argumentsList) {
		super.getArgsAcceptor(argumentsList.length - 1);
		if(super.hasError) {
			return;
		}
		
		File filePath = new File(argumentsList[1]);
		
		if(!filePath.isAbsolute()) {
			filePath = new File(Shell.currentDirectory+ File.separator + argumentsList[1]);
		}
		if(!filePath.exists() || !filePath.isDirectory()) {
			Shell.generateAnError("\"" + argumentsList[1] + "\": No such directory.", this.getName());
			return;
		}
		Shell.currentDirectory = filePath;
	}
}
