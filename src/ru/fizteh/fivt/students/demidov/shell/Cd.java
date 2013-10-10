package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.io.IOException;

public class Cd implements BasicCommand {
	public void executeCommand(String[] arguments) throws IOException {	
		File goToDirectory = Utils.getFile(arguments[0]);

		if (goToDirectory.isDirectory()) {
			Shell.changeCurrentDirectory(goToDirectory.getPath());
		} else {
			throw new IOException(arguments[0] + ": no such directory");	
		}
	}
}