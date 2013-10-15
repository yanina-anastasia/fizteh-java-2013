package ru.fizteh.fivt.students.msandrikova.shell;

import java.io.File;
import java.io.IOException;

public class PrintWorkingDirectoryCommand extends Command {
	
	public PrintWorkingDirectoryCommand() {
		super("pwd", 0);
	}

	@Override
	public File execute(String[] argumentsList, boolean isInteractive, File currentDirectory) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, isInteractive)) {
			return currentDirectory;
		}

		try {
			String filePath = currentDirectory.getCanonicalPath();
			System.out.println(filePath);
		} catch (IOException e) {}
		return currentDirectory;
	}
	
}
