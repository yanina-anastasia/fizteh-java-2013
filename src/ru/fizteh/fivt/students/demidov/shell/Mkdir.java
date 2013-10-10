package ru.fizteh.fivt.students.demidov.shell;

import java.io.IOException;

public class Mkdir implements BasicCommand {
	public void executeCommand(String[] arguments) throws IOException {		
		if (!Utils.getFile(arguments[0]).mkdir()) {
			throw new IOException("unable to make directory " + arguments[0]);
		}
	}
}