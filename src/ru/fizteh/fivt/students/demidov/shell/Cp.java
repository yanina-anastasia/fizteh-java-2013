package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.io.IOException;

public class Cp implements BasicCommand {
	public void executeCommand(String[] arguments) throws IOException {	
		File source = Utils.getFile(arguments[0]);
		if (source.exists()) {
			Utils.copyFileOrDirectory(source, Utils.getFile(arguments[1]));
		} else {
			throw new IOException(source.getPath() + " doesn't exist");
		}
	}
}
