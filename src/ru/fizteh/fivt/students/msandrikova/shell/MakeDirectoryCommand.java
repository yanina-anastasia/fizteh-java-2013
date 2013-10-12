package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;

public class MakeDirectoryCommand extends Command {

	public MakeDirectoryCommand() {
		super("mkdir", 1);
	}

	@Override
	public File execute(String[] argumentsList, boolean isInteractive, File currentDirectory) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, isInteractive)) {
			return currentDirectory;
		}
		
		File fileName = new File(currentDirectory + File.separator + argumentsList[1]);
		if(fileName.exists()) {
			Utils.generateAnError("Directory with name \"" + argumentsList[1] + "\" already exists", this.getName(), isInteractive);
			return currentDirectory;
		}
		try {
			if(!fileName.mkdirs()) {
				Utils.generateAnError("Directory with name \"" + argumentsList[1] + "\" can not be created", this.getName(), isInteractive);
				return currentDirectory;
			};
		} catch (SecurityException e) {
			Utils.generateAnError("Directory with name \"" + argumentsList[1] + "\" can not be created", this.getName(), isInteractive);
			return currentDirectory;
		}
		return currentDirectory;
	}
}
