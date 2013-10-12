package ru.fizteh.fivt.students.demidov.shell;

import java.io.IOException;

public class Mkdir implements BasicCommand {
	public void executeCommand(String[] arguments, Shell.CurrentShell curShell) throws IOException {		
		if (!Utils.getFile(arguments[0], curShell).mkdir()) {
			throw new IOException("unable to make directory " + arguments[0]);
		}
	}
}