package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.IOException;

public class RemoveFileOrDirectoryCommand extends Command {

	public RemoveFileOrDirectoryCommand() {
		super("rm", 1);
	}
	
	@Override
	public File execute(String[] argumentsList, boolean isInteractive, File currentDirectory) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, isInteractive)) {
			return currentDirectory;
		}
		
		File filePath = new File(currentDirectory + File.separator + argumentsList[1]);
		
		try {
			if(!Utils.remover(filePath, this.getName(), isInteractive)) {
				return currentDirectory;
			}
		} catch (IOException e) {}
		return currentDirectory;
	}

}
