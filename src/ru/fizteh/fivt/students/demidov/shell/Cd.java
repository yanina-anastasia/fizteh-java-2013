package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.io.IOException;

public class Cd implements BasicCommand {
	public void executeCommand(String[] arguments, Shell.CurrentShell curShell) throws IOException {	
		File goToDirectory = Utils.getFile(arguments[0], curShell);

		if (goToDirectory.isDirectory()) {
			curShell.changeCurrentDirectory(goToDirectory.getPath());
		} else {
			throw new IOException(arguments[0] + ": no such directory");	
		}
	}
}