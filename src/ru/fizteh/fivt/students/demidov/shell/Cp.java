package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.io.IOException;

public class Cp implements BasicCommand {
	public void executeCommand(String[] arguments) throws IOException {	
		File destination = Utils.getFile(arguments[1]);

		if (destination.isDirectory()) {
			Utils.copyFileOrDirectory(Utils.getFile(arguments[0]), destination);
		} else {
			throw new IOException(arguments[1] + " is not a directory");
		}
	}
}
